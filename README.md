# Royal Mail Demo

Royal Mail Demo + Augmate SDK is a Google Glass optimized optical bar-code scanning package that comes with full source-code.  

This was created to help internal R&D departments experiment with new ideas.  

## Dependencies
* [Standalone Android SDK Tools](https://developer.android.com/sdk/index.html) is available under the heading "Get SDK for an existing IDE"  
  Mixing IntelliJ with the Eclipse ADT Bundle is possible, but may require extra work.  
* [32-bit Android NDK](https://developer.android.com/tools/sdk/ndk/index.html)  
* [Latest version of IntelliJ on JetBrains' EAP page](https://confluence.jetbrains.com/display/IDEADEV/IDEA+14+EAP).  
The Community Edition works if you don't yet have a paid license for this amazing IDE.

## Getting Started

Install:  
```shell
git clone https://github.com/augmate/royal-mail-optical-package.git
cd royal-mail-optical-package
```

Update `local.properties` file with your sdk.dir and ndk.dir paths:
```text
ndk.dir=/absolute/path/android-ndk
sdk.dir=/absolute/path/android-sdk
```

Build from source the quick console way:  
```shell
./gradlew build -x preDexDebug -x preDexRelease -x lint
```

Install Royal Mail apk onto your Glass:  
Generated apks are found in: `royal-mail/build/outputs/apk/`  
```shell
adb install royal-mail/build/outputs/apk/royal-mail-release.apk
```

## Building an Android app using IntelliJ

Start IntelliJ, Import Project, select the `royal-mail-optical-package/build.gradle` file. Toggle "use default gradle wrapper" and leave the other defaults alone.

## On Gradle/IntelliJ
First-time setup of IntelliJ+Gradle Android dev environments can be rough. It's worth it.  
Gradle files are in charge of the project, importing any other way is unsupported and may result in a broken project.
