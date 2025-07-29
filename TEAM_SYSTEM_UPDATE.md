# 🎯 BingoReloaded 队伍系统更新

## ✅ 新增功能

### 1. 队伍管理指令 (`/bingo team`)

新增了完整的队伍管理指令系统，所有玩家都可以使用：

#### 🔧 可用子命令：

- **`/bingo team create <name> [color]`** - 创建新队伍
  - 支持16进制颜色 (`#FF0000`) 或颜色名称 (`red`)
  - 创建者自动加入队伍
  
- **`/bingo team join <name>`** - 加入指定队伍
  - 检查队伍容量限制
  - 自动离开当前队伍
  
- **`/bingo team leave`** - 离开当前队伍
  
- **`/bingo team list`** - 查看所有活跃队伍
  - 显示队伍名称、颜色、成员数量
  - 列出所有队伍成员

- **`/bingo team invite <player>`** - 邀请玩家（占位符功能）
- **`/bingo team kick <player>`** - 踢出队伍成员（占位符功能）

#### 📝 使用示例：
```
/bingo team create Warriors red
/bingo team create Builders #00FF00
/bingo team join Warriors
/bingo team list
/bingo team leave
```

### 2. 智能观赛模式

更新了队伍管理逻辑，确保没有队伍的玩家自动变成观赛者：

#### 🎮 游戏逻辑：

**游戏未开始时：**
- 新玩家加入 → 自动分配到 "auto" 队伍进行队伍选择
- 已有队伍的玩家 → 保持队伍状态

**游戏进行中时：**
- 有真实队伍的玩家 → 正常游戏模式 (`GameMode.SURVIVAL`)
- 只在 "auto" 队伍或无队伍的玩家 → 观赛模式 (`GameMode.SPECTATOR`)
- 新加入的玩家 → 自动观赛模式

#### 🔄 支持两种队伍管理器：
- **BasicTeamManager** - 普通多人队伍模式
- **SoloTeamManager** - 单人队伍模式

## 🛠 技术实现

### 代码变更：

1. **BingoCommand.java**：
   - 新增 `handleTeamCommand()` 方法
   - 完整的子命令处理逻辑
   - Tab completion 支持
   - 导入必要的类 (`TeamData`, `BingoTeam`, `TextColor`)

2. **BasicTeamManager.java**：
   - 更新 `handlePlayerJoinedSessionWorld()` 逻辑
   - 智能区分真实队伍和auto队伍
   - 游戏状态感知的观赛模式分配

3. **SoloTeamManager.java**：
   - 统一的观赛模式处理
   - 单人模式下的队伍验证

### 🎯 设计特点：

- **向后兼容**：保持原有队伍选择菜单功能
- **权限友好**：普通玩家可创建和管理队伍
- **状态感知**：根据游戏状态智能处理玩家模式
- **类型安全**：完整的错误处理和验证

## 📊 功能对比

| 功能 | 之前 | 现在 |
|-----|------|------|
| 队伍创建 | 仅管理员通过GUI | 所有玩家通过命令 |
| 队伍加入 | GUI选择菜单 | 命令 + GUI选择 |
| 观赛模式 | 仅游戏开始后 | 智能自动分配 |
| 队伍列表 | 仅管理员可查看 | 所有玩家可查看 |

## 🚀 下一步可能的扩展

1. **队伍邀请系统** - 完整的邀请/接受机制
2. **队长权限** - 队伍创建者的管理权限
3. **队伍聊天** - 独立的队伍聊天频道
4. **队伍统计** - 队伍表现数据追踪

---

✅ **状态**: 已完成并测试通过  
🔨 **版本**: 兼容 BingoReloaded 3.2.0  
📦 **构建**: 已生成 `BingoReloaded-3.2.0-all.jar`