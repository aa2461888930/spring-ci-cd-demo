# 给「只会点 IDEA 的 Push」的人的 git 命令行手册

> 全程用你自己的机器、真实的命令、真实的回显。照着敲就行。
> 练习在 `git-lab/` 里做，弄坏了删掉重来，不影响正经项目。

---

## 0. 先破一个心理障碍

你在 IDEA 里点的每个按钮，背后就是一条命令。没有魔法：

| IDEA 里点的 | 实际执行的命令 |
|---|---|
| Commit（勾文件 → Commit） | `git add <文件>` + `git commit -m "..."` |
| Commit and Push | 上面两条 + `git push` |
| Push（右上角箭头） | `git push` |
| Pull / Update Project | `git pull` |
| 右下角分支 → New Branch | `git switch -c 分支名` |
| 右下角分支 → Checkout | `git switch 分支名` |
| Git → Merge into Current | `git merge 分支名` |
| Git → Show History | `git log` |
| 右键文件 → Git → Rollback | `git restore 文件` |
| Git → Unstage | `git restore --staged 文件` |
| Git → Revert Commit | `git revert <提交号>` |
| Git → Tag | `git tag v1.0.0` |

所以你不是"不会 git"，你是**没建立模型**。下面这个模型一旦立起来，命令就不用背了。

---

## 1. 唯一需要记住的模型：三个区

```
   你的文件夹            暂存区              本地版本库           远程（GitHub）
  ┌─────────┐        ┌─────────┐        ┌─────────┐        ┌─────────┐
  │ 工作区   │──add──▶│ staging │──commit▶│  .git   │──push──▶│ origin  │
  └─────────┘        └─────────┘        └─────────┘        └─────────┘
       ▲                                      │
       └──────────── restore ─────────────────┘
```

| 命令 | 动的是哪个区 | 一句话 |
|---|---|---|
| `git status` | 只看，不动 | 我现在处在什么状态 |
| `git add 文件` | 工作区 → 暂存区 | 把改动"放进篮子" |
| `git commit` | 暂存区 → 本地库 | 把篮子"封箱存档" |
| `git push` | 本地库 → 远程 | 把箱子"寄出去" |
| `git pull` | 远程 → 本地库 + 工作区 | 把别人的箱子"取回来" |
| `git restore 文件` | 本地库 → 工作区 | 丢掉还没 add 的改动 |

**最常见的误解**：以为 `commit` 就等于上传了。**不是。commit 只到本地**，`push` 才出门。这一点后面第 6 节会用实验证明，它直接决定 GitHub Actions 什么时候被触发。

---

## 2. 体检三连（每天开工先跑）

```bash
git status                 # 我在哪个分支，有什么改动
git log --oneline --graph  # 最近提交长什么样
git remote -v              # 我连的是哪个远程
```

真实回显（在练习仓库里）：

```console
$ git status
On branch main
Your branch is up to date with 'origin/main'.

nothing to commit, working tree clean

$ git log --oneline --graph -3
* 935fee7 docs: 添加第四行（信息已修正）
* 5846229 临时提交信息写错了
*   f5b8b37 merge: 合并 feature/add-title

$ git remote -v
origin	../git-lab-origin.git (fetch)
origin	../git-lab-origin.git (push)
```

> `git status -sb` 是精简版，我平时就用这个：`## main...origin/main [ahead 2]` 一眼看出"领先远程 2 个提交"。

---

## 3. 搭一个练习沙盒（5 分钟，弄坏也不心疼）

先做一个"假 GitHub"（bare 仓库，只存数据，没有工作目录）：

```bash
cd ~/Documents/deepseek-harness/default-workspace
git init --bare git-lab-origin.git      # 这就是"服务器"
mkdir git-lab && cd git-lab
git init -b main
```

> 工作区里我已经帮你建好了 `git-lab/` 和 `git-lab-origin.git`，可以直接进去练。

---

## 4. 第一次提交：完整走一遍

### ① 看现状

```console
$ git status
On branch main

No commits yet

nothing to commit (create/copy files and use "git add" to track)
```

### ② 建个文件，再看

```console
$ echo "# 我的第一个仓库" > notes.md
$ git status -s
?? notes.md
```

`??` = **未跟踪**（git 还不认识它）。

### ③ 放进暂存区

```console
$ git add notes.md
$ git status
On branch main

No commits yet

Changes to be committed:
  (use "git rm --cached <file>..." to unstage)
	new file:   notes.md
```

文件从"未跟踪"变成"待提交"。

### ④ 提交

```console
$ git commit -m "docs: 添加笔记"
[main (root-commit) 7d69115] docs: 添加笔记
 1 file changed, 1 insertion(+)
 create mode 100644 notes.md

$ git log --oneline
7d69115 docs: 添加笔记
```

`7d69115` 就是提交号，后面撤销、回滚都要用它（写前 7 位就够）。

### ⑤ 改一行，看 diff

```console
$ echo "第二行：今天开始学 git" >> notes.md
$ git status -s
 M notes.md

$ git diff
diff --git a/notes.md b/notes.md
index 8712b0d..805c596 100644
--- a/notes.md
+++ b/notes.md
@@ -1 +1,2 @@
 # 我的第一个仓库
+第二行：今天开始学 git
```

` M` 前面有空格 = **已修改但没 add**；`M ` 后面有空格 = **已 add**。这个空格位置是状态，别看漏。

### ⑥ 提交这次改动

```bash
git add notes.md
git commit -m "docs: 补充第二行"
```

> 偷懒写法：`git commit -am "..."` 会自动把**已跟踪**文件加进来（新增的未跟踪文件不算）。

---

## 5. 连上 GitHub 并推送（你现在真正要做的）

### ① 先在网页上建仓库

打开 https://github.com/new

- 名字：`spring-ci-cd-demo`
- 选 **Public**（公开仓库跑 Actions 不花钱）
- **不要**勾 "Add a README"、".gitignore"、license（本地已经有了，勾了会冲突）

### ② 关联远程

```bash
cd spring-ci-cd-demo
git remote add origin https://github.com/<你的用户名>/spring-ci-cd-demo.git
git remote -v          # 确认加上了
```

### ③ 推送

```bash
git push -u origin main
```

真实回显（练习仓库里）：

```console
$ git push -u origin main
To ../git-lab-origin.git
 * [new branch]      main -> main
branch 'main' set up to track 'origin/main'.
```

`-u` 的意思是"记住这个对应关系"，**以后直接 `git push` 就行**，不用再写 `origin main`。

### ④ 验证真的上去了

```bash
git status -sb
# ## main...origin/main        ← 两边齐平，没有 ahead/behind
```

---

## 6. 认证：我有 SSH key，为什么 push 还要密码？

因为 **HTTPS 和 SSH 是两扇不同的门**：

| 远程地址长这样 | 用什么认证 | 会问密码吗 |
|---|---|---|
| `https://github.com/用户/仓库.git` | 用户名 + Token | 会（要粘贴 token） |
| `git@github.com:用户/仓库.git` | SSH 私钥 | **不会** |

SSH key 只对 `git@` 开头的地址生效。`git remote add` 时写了 `https://`，那把 key 再完好也用不上——这就是"明明有 key 还要密码"的全部原因。

顺带解释 IDEA：它不问密码，是因为它把 GitHub 的 OAuth 令牌存在自己的密码库里了，跟 SSH key 无关。

### 你机器上的实测

```console
$ ssh -T git@github.com
Hi aa2461888930! You've successfully authenticated, but GitHub does not provide shell access.

$ ssh -T -i ~/.ssh/id_ed25519 git@github.com
git@github.com: Permission denied (publickey).
```

| 密钥 | 状态 |
|---|---|
| `~/.ssh/id_rsa`（3072 位 RSA） | ✅ 已注册，可用 |
| `~/.ssh/id_ed25519` | ❌ 未注册 |

**结论：直接用 `id_rsa` 走 SSH，全程不输密码。**

### 用 SSH 推送

```bash
# 1. 验证能连上（看到 Hi 你的用户名 就对了）
ssh -T git@github.com

# 2. 远程地址用 SSH 形式（注意是冒号，不是斜杠）
git remote add origin git@github.com:aa2461888930/spring-ci-cd-demo.git

# 3. 推
git push -u origin main
```

已经加过 HTTPS 地址的，改一下就行：

```bash
git remote set-url origin git@github.com:aa2461888930/spring-ci-cd-demo.git
```

### 建议：再补一把 ed25519（RSA 3072 有点老）

```bash
ssh-keygen -t ed25519 -C "2461888930@qq.com"   # 一路回车
pbcopy < ~/.ssh/id_ed25519.pub                  # 已复制到剪贴板
# 粘贴到 https://github.com/settings/keys → New SSH key
```

### 兜底：公司网络封了 22 端口怎么办

在 `~/.ssh/config` 里加：

```
Host github.com
  HostName ssh.github.com
  Port 443
```

`ssh -T git@github.com` 再试一次。走 443 端口，防火墙通常不拦。

### 最后的兜底：Token

实在要用 HTTPS（比如某些 CI 环境）：

1. https://github.com/settings/tokens → Generate new token (classic)
2. 勾 `repo` + `workflow`（推 `.github/workflows` 必须有）
3. push 时用户名填 GitHub 名，**密码位置粘贴 token**

```bash
git config --global credential.helper osxkeychain   # 记住，下次不用再输
```

---

## 7. 分支与 PR（对应 IDEA 右下角那个分支菜单）

### ① 开分支

```console
$ git switch -c feature/add-title
Switched to a new branch 'feature/add-title'

$ git status -sb
## feature/add-title
```

`switch -c` = 新建并切过去。旧写法 `git checkout -b` 也行。

### ② 在分支上干活并提交

```bash
echo "第三行：分支上写的" >> notes.md
git add -A
git commit -m "feat: 加第三行"
```

### ③ 推分支上去（**PR 之前必须推**）

```console
$ git push -u origin feature/add-title
 * [new branch]      feature/add-title -> feature/add-title
branch 'feature/add-title' set up to track 'origin/feature/add-title'.
```

推上去之后，GitHub 页面上会出现 **Compare & pull request** 按钮，点它就能提 PR。

### ④ 回到 main 合并

```console
$ git switch main
$ git merge --no-ff feature/add-title -m "merge: 合并 feature/add-title"
Merge made by the 'ort' strategy.
 notes.md | 1 +
 1 file changed, 1 insertion(+)

$ git log --oneline --graph -4
*   f5b8b37 merge: 合并 feature/add-title
|\
| * 58459b9 feat: 加第三行
|/
* 8a0b551 docs: 补充第二行
```

`--no-ff` 强制保留"这是一个分支合并"的痕迹，历史更清楚。

### ⑤ 删掉用完的分支

```bash
git branch -d feature/add-title              # 删本地
git push origin --delete feature/add-title   # 删远程
```

---

## 8. commit ≠ push（这一课直接关系到你的流水线）

我刚在练习仓库里做了个实验：

```console
# 本地合并完成，服务器上还是旧的
$ git --git-dir=../git-lab-origin.git log --oneline -1
8a0b551 docs: 补充第二行

$ git push origin main
$ git --git-dir=../git-lab-origin.git log --oneline -1
f5b8b37 merge: 合并 feature/add-title
```

**结论**：

- `commit` 只写进你电脑上的 `.git`
- `push` 才真正到 GitHub
- **GitHub Actions 只在 push（或 PR、tag）时触发**——你 commit 了 10 次没 push，流水线一次都不会跑

这就是为什么"我代码改了，Actions 怎么没动"——九成是忘了 push。

---

## 9. 撤销三连（改错了怎么办）

### ① 工作区改坏了，还没 add

```console
$ echo "手滑写坏内容" > notes.md
$ git restore notes.md          # 丢掉这些改动，回到上次提交的样子
```

> 老写法 `git checkout -- notes.md`。**注意：这会永久丢弃改动，没有回收站。**

### ② 已经 add 了，想撤回暂存区

```console
$ git add notes.md
$ git restore --staged notes.md   # 从暂存区拿出来，改动还在文件里
$ git restore notes.md            # 再丢掉改动本身
```

### ③ 提交信息写错了

```console
$ git commit --amend -m "chore: 正确的提交信息"
```

只改最近一次提交。**已经 push 过的不要 amend**，会导致历史不一致（要 `push -f`，很危险）。

> 我演示时就翻车了一次：空提交 amend 完还是空的，git 直接拒绝并提示 `You can repeat your command with --allow-empty`。**git 报错时不会改坏东西**，看清楚再操作就行。

### ④ 想撤销一个已经提交的改动（安全做法）

```bash
git revert <提交号>      # 生成一个新提交，内容是"反向操作"
```

比 `reset --hard` 安全得多，因为它不删历史，团队协作不会打架。

---

## 10. 打 tag 发版

```console
$ git tag v1.0.0
$ git push origin v1.0.0     # tag 必须单独推，git push 不会带上

$ git tag -l
v1.0.0
```

流水线里配了 `tags: [ 'v*' ]`，所以推 tag 会自动触发一次，可以拿来做 release。

---

## 11. 冲突：两个人改了同一行

```bash
git pull            # 冲突时 git 会说 CONFLICT
git status          # 列出"both modified"的文件
```

打开文件，你会看到：

```
<<<<<<< HEAD
你这边的版本
=======
远程来的版本
>>>>>>> origin/main
```

**手动改成你想要的样子**（把三行标记全删掉），然后：

```bash
git add 冲突文件
git commit -m "merge: 解决冲突"
```

> 记住：冲突不是错误，是 git 在问你"这两份你要哪个"。改完 add + commit 就结束了。

---

## 12. 危险命令清单（先认识，别乱敲）

| 命令 | 后果 | 什么时候能用 |
|---|---|---|
| `git reset --hard` | 丢弃所有未提交改动 | 只在确认不要了的时候 |
| `git push -f` | 覆盖远程历史 | 只在自己的分支，且团队同意 |
| `git clean -fd` | 删掉未跟踪文件 | 确认没有新文件没 add |
| `git checkout <文件>` | 丢弃工作区改动 | 同 reset --hard，不可恢复 |

---

## 13. 每天要用的就 6 条

```bash
git status -sb                 # 我在哪、有什么改动
git diff                       # 改了什么
git add -A && git commit -m "xxx"   # 存档
git push                       # 寄出去
git pull                       # 取回来
git switch -c feature/xxx      # 开新分支
```

**记住这 6 条，你已经比 90% 只会点 Push 的人清楚了。**

---

## 14. 现在，把这个项目推上去（照抄，SSH 版）

```bash
cd ~/Documents/deepseek-harness/default-workspace/spring-ci-cd-demo

# 0. 确认 SSH 通（期望：Hi aa2461888930! You've successfully authenticated）
ssh -T git@github.com

# 1. 网页建仓：github.com/new → spring-ci-cd-demo → Public → 什么都别勾

# 2. 关联远程
git remote add origin git@github.com:aa2461888930/spring-ci-cd-demo.git

# 3. 确认加对了
git remote -v

# 4. 推
git push -u origin main

# 5. 验证：两边齐平就是成功
git status -sb          # 期望 ## main...origin/main
```

### 一个容易忽略的小事：邮箱要和 GitHub 对上

否则提交记录不显示你的头像、贡献图也不绿。

```bash
git config --global user.email   # 现在: 2461888930@qq.com
```

如果这个邮箱不是 GitHub 账号里绑定的那个，改成绑定的那个；不想暴露真实邮箱就用 GitHub 给的不公开地址：

```bash
git config --global user.email "<你的ID>+<用户名>@users.noreply.github.com"
# 在 GitHub → Settings → Emails 页面能看到确切地址
```

已经提交的历史不受影响，之后的新提交会用新邮箱。

---

## 附：常见报错对照表

| 报错 | 含义 | 怎么办 |
|---|---|---|
| `remote origin already exists` | 已经加过远程 | `git remote set-url origin <新地址>` |
| `rejected (non-fast-forward)` | 远程有你本地没有的提交 | 先 `git pull --rebase`，再 push |
| `Authentication failed` | 用了密码而不是 token | 见第 6 节 |
| `Permission denied (publickey)` | 这把 SSH key 没注册到 GitHub | `ssh -T -i <key> git@github.com` 逐个排查 |
| `Could not read from remote repository` | 没权限 / 地址写错 / 没建仓库 | 检查 `git remote -v` 和仓库是否存在 |
| `src refspec main does not match any` | 本地还没有 main 分支/提交 | 先 `git add . && git commit` |
| `fatal: not a git repository` | 不在仓库目录里 | `cd` 到有 `.git` 的目录，用 `git rev-parse --show-toplevel` 找 |
| `Please tell me who you are` | 没配用户名邮箱 | `git config --global user.name/user.email` |
