package io.github.steaf23.bingoreloaded.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.playerdisplay.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CardHttpClient {
    private static final String API_ENDPOINT = "/api/bingo/card";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .protocols(Arrays.asList(Protocol.HTTP_1_1)) // Force HTTP/1.1
            .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    private static final Gson gson = new Gson();
    private static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE; // zh_CN

    private static String renderToLocale(Component component, Locale locale) {
        if (component == null) {
            return "";
        }
        Component rendered = GlobalTranslator.render(component, locale);
        return PlainTextComponentSerializer.plainText().serialize(rendered);
    }

    private static String typeLabel(TaskData.TaskType type) {
        // 保持后端可解析的英文枚举不变，额外提供中文可读标签
        return switch (type) {
            case ITEM -> "物品";
            case ADVANCEMENT -> "成就";
            case STATISTIC -> "统计";
        };
    }

    /**
     * POST card data to the API endpoint with retry logic
     * @param card The TaskCard to send
     * @param baseUrl The base URL of the API server (e.g., "http://localhost:8080")
     * @param team The team information (null for shared cards)
     */
    public static void postCardData(TaskCard card, String baseUrl, BingoTeam team) {
        // Convert card to JSON
        JsonObject cardData = convertCardToJson(card, team);
        String json = gson.toJson(cardData);
        
        // Perform POST with retry
        postWithRetry(baseUrl + API_ENDPOINT, json, 1);
    }

    private static JsonObject convertCardToJson(TaskCard card, BingoTeam team) {
        JsonObject cardJson = new JsonObject();
        
        // Get card size information
        cardJson.addProperty("size", card.size.fullCardSize);
        cardJson.addProperty("width", card.size.size);
        cardJson.addProperty("height", card.size.size);
        
        // Add team information if provided
        if (team != null) {
            JsonObject teamJson = new JsonObject();
            teamJson.addProperty("name", renderToLocale(team.getName(), DEFAULT_LOCALE));
            teamJson.addProperty("color", team.getColor().toString());
            teamJson.addProperty("completeCount", team.getCompleteCount());
            teamJson.addProperty("outOfTheGame", team.outOfTheGame);
            
            // Add team members
            JsonObject membersJson = new JsonObject();
            team.getMembers().forEach(member -> {
                JsonObject memberJson = new JsonObject();
                memberJson.addProperty("name", member.getName());
                memberJson.addProperty("displayName", renderToLocale(member.getDisplayName(), DEFAULT_LOCALE));
                memberJson.addProperty("alwaysActive", member.alwaysActive());
                membersJson.add(member.getName(), memberJson);
            });
            teamJson.add("members", membersJson);
            
            cardJson.add("team", teamJson);
        }
        
        // Get tasks data
        List<GameTask> tasks = card.getTasks();
        JsonObject tasksJson = new JsonObject();
        
        for (int i = 0; i < tasks.size(); i++) {
            GameTask task = tasks.get(i);
            JsonObject taskJson = new JsonObject();
            
            // Calculate relative coordinates (x, y) from index
            int x = i % card.size.size;  // Column (0-based)
            int y = i / card.size.size;  // Row (0-based)
            
            taskJson.addProperty("index", i);
            taskJson.addProperty("x", x);
            taskJson.addProperty("y", y);
            taskJson.addProperty("name", renderToLocale(task.data.getName(), DEFAULT_LOCALE));
            taskJson.addProperty("type", task.data.getType().toString());
            taskJson.addProperty("typeLabel", typeLabel(task.data.getType()));
            taskJson.addProperty("description", renderToLocale(task.data.getChatDescription(), DEFAULT_LOCALE));
            
            // Add task material/item information if available
            if (task.data instanceof ItemTask itemTask) {
                taskJson.addProperty("material", itemTask.material().toString());
                taskJson.addProperty("count", itemTask.count());
                taskJson.addProperty("materialLabel", renderToLocale(ComponentUtils.itemName(itemTask.material()), DEFAULT_LOCALE));
            }

            // Add advancement info (translated) if applicable
            if (task.data instanceof AdvancementTask advTask && advTask.advancement() != null) {
                var adv = advTask.advancement();
                taskJson.addProperty("advancementId", adv.getKey().toString());
                taskJson.addProperty("advancementTitle", renderToLocale(ComponentUtils.advancementTitle(adv), DEFAULT_LOCALE));
                taskJson.addProperty("advancementDescription", renderToLocale(ComponentUtils.advancementDescription(adv), DEFAULT_LOCALE));
            }
            
            // Use coordinate-based key for better organization
            tasksJson.add(x + "," + y, taskJson);
        }
        
        cardJson.add("tasks", tasksJson);
        cardJson.addProperty("timestamp", System.currentTimeMillis());
        
        return cardJson;
    }

    private static void postWithRetry(String url, String json, int attempt) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ConsoleMessenger.error("Failed to POST card data (attempt " + attempt + "): " + e.getMessage());
                
                // Retry after a delay
                BingoReloaded.scheduleTask(task -> {
                    postWithRetry(url, json, attempt + 1);
                }, 20L * attempt); // Exponential backoff: 1s, 2s, 3s, etc.
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ConsoleMessenger.log("Successfully posted card data to " + url + " (attempt " + attempt + ")");
                } else {
                    ConsoleMessenger.error("Failed to POST card data (attempt " + attempt + "): HTTP " + 
                            response.code() + " - " + response.message());
                    
                    // Retry for non-successful responses
                    BingoReloaded.scheduleTask(task -> {
                        postWithRetry(url, json, attempt + 1);
                    }, 20L * attempt); // Exponential backoff
                }
                response.close();
            }
        });
    }
}