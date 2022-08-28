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
RUN mv cmdline-tools android/sdk

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
