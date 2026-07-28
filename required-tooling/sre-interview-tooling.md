# SRE Interview required Tooling

The SRE interview is a **live troubleshooting session on a remote Kubernetes
environment**. You will connect to a management host over **VSCode Remote-SSH** —
all the tooling (`kubectl`, `helm`, `docker`, the repository) is already installed
there, so there is **nothing to install locally besides VSCode and an SSH client**.

## On your machine

- Download VS Code from https://code.visualstudio.com/
- Install the following plugins:
    - Remote - SSH - https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-ssh
    - Live Share - https://marketplace.visualstudio.com/items?itemName=MS-vsliveshare.vsliveshare
    - YAML - https://marketplace.visualstudio.com/items?itemName=redhat.vscode-yaml
    - Kubernetes - https://marketplace.visualstudio.com/items?itemName=ms-kubernetes-tools.vscode-kubernetes-tools
- Make sure you have an OpenSSH client on your `PATH` (`ssh -V`)
    - macOS / Linux: already there
    - Windows: install the *OpenSSH Client* optional feature, or use WSL

## Before the interview — send us these two things

Please send these **a few hours before the session** so we don't lose interview
time on setup.

1. **Your SSH public key.** If you don't have one, generate it:

   ```bash
   ssh-keygen -t ed25519 -C "your-name@laptop"
   ```

   Send us the contents of `~/.ssh/id_ed25519.pub` (the `.pub` file only —
   never share your private key).

2. **Your public IP address**, so we can white-list it on the firewall:

   ```bash
   curl https://checkip.amazonaws.com
   ```

   If your IP isn't stable (VPN, mobile hotspot), let us know and we'll open
   access more broadly.

## Set up the SSH host entry

We will send you the host IP. Add this entry to your `~/.ssh/config`
(`C:\Users\<you>\.ssh\config` on Windows), filling in the IP we give you:

```sshconfig
Host sre-interview-mgmt
    HostName <ip-we-send-you>
    User sre
    Port 2223
    IdentityFile ~/.ssh/id_ed25519
    StrictHostKeyChecking no
    UserKnownHostsFile /dev/null
```

## Verify it works

The environment is only live during the interview, so you can't connect ahead of
time — but do the following so the mechanics are familiar:

- Confirm **Remote-SSH: Connect to Host…** shows up in the VSCode command palette
  (`Cmd/Ctrl` + `Shift` + `P`) and lists `sre-interview-mgmt`
- Remote-SSH into any Linux box you have handy (a VM, a cloud host, even
  `localhost` with Remote Login enabled) at least once, so the first-connect
  flow — VSCode installing its server, trusting the host, opening a remote
  folder and a remote terminal — isn't new to you on the day
- Note that VSCode extensions install **per remote host**: after connecting,
  extensions like YAML/Kubernetes may need an *Install in SSH: …* click. That's
  expected and takes seconds

## On the day

- We'll confirm your key and IP are authorized, then you connect with
  **Remote-SSH: Connect to Host… → `sre-interview-mgmt`**
- Open the folder `~/sre` and a remote terminal
  (`Terminal → New Terminal` — it runs *on the remote host*)
- Everything you need (`kubectl`, `helm`, `docker`, kubeconfig, SSH access to the
  cluster nodes) is already installed and wired up there
- Get a bit familiar with the VSCode interface, but don't overdo it, we'll keep
  things light
