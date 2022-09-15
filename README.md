
1. Создаем Dockerfile для teamcity agent в директории (Дополнительно про агента https://hub.docker.com/r/jetbrains/teamcity-agent)

```Dockerfile
FROM jetbrains/teamcity-agent

USER root
RUN yes | apt-get update
RUN yes | apt-get upgrade
RUN yes | apt-get install unzip wget curl sudo


# Here we download Android CLI Tools (one of them is sdkmanager that will help us to download other tools)
# sdkmanager https://developer.android.com/studio/command-line/sdkmanager
# Android Studio and sdkmanager download page https://developer.android.com/studio#downloads
RUN yes | wget https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip
RUN unzip commandlinetools-linux-8512546_latest.zip
RUN rm commandlinetools-linux-8512546_latest.zip

# Extracted tools will be in "cmdline-tools" directory. Move all tools into "android/sdk" directory
RUN mkdir -p android/sdk
RUN  mv cmdline-tools android/sdk

# Add Some default environment variables for Android
ENV ANDROID_HOME=/android/sdk
ENV ANDROID_SKD_ROOT=$ANDROID_HOME

# Install Android tools
# Build tools https://developer.android.com/studio/releases/build-tools
# Platform tools https://developer.android.com/studio/releases/platform-tools
# Platform (Android API) https://developer.android.com/studio/releases/platforms
# ------------------------------------------------------------------------------
# Here we download platform Android 13/Tiramisu (API level 33)
RUN yes | /android/sdk/cmdline-tools/bin/sdkmanager --install --sdk_root=${ANDROID_HOME} --no_https "platforms;android-33" "build-tools;30.0.2"
# Install firebase CLI (Downloaded script from link and run it)
RUN yes | curl -sL https://firebase.tools | bash

```

2. Билдим docker image для teamcity агента
```bash
docker build -t android-agent-image .
```

3. Запускаем агента (ip докера можно посмотреть в интерфейсах ifconfig)
```bash
docker run -d --name android-agent -e SERVER_URL="172.17.0.1:8111" -e AGENT_NAME="android-agent" android-agent-image
```

5. Запускаем docker контейнер для teamcity сервера (Доп параметры тут https://hub.docker.com/r/jetbrains/teamcity-server)
```bash
docker run -d --name android-server-image -p 8111:8111 jetbrains/teamcity-server
```

6. В Веб-интерфейсе в агентах видим неавторизованного агента. Авторизовываем его

7. Генерируем у себя ssh ключ.  (passprhase ключевое слово для доступа к приватному ключу)
```bash
ssh-keygen -f teamcity_rsa -N passphrase -t rsa -m PEM
```
8. Заходим в Administration -> Projects -> Root Project -> SSH keys И добавляем сюда teamcity_rsa файл, который мы сгенерировали
9. В github добавляем ssh ключ скопированный из teamcity_rsa.pub
10. Создаем пустой проект на github и копируем ссылку на него в формате https://github.com/Hiraev/public-teamcity-settings.git
11. Открывает в Teamcity Administration -> Projects -> Root Project -> VCS Roots -> Create VCS Roots
В repository url указываем скопированный на шаге 10 url. (В проекте должная быть хотя бы одна ветка). При подключении будет использоваться ключ, который мы добавили на шаге 7-9.
