import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.buildReportTab
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2020.1"

project {
    description = "Contains all other projects"

    vcsRoot(HttpsGithubComHiraevPublicTeamcitySettingsGit)
    cleanup {
        baseRule {
            preventDependencyCleanup = false
        }
    }

    subProject {
        id("TheGuardianNews_App")
        name = "TheGuardianNews App"

        vcsRoot(TheGuardianNewsVcs)

        params {
            text("env.AAA", "fds")
            param("env.CCCCC", "true")
        }
        buildType {
            vcs {
                root(TheGuardianNewsVcs)
            }
            id("MyBuild")
            name = "Just a Testing Build"


            steps {
                gradle {
                    buildFile = "build.gradle.kts"
                    enabled = true
                    name = "prebuild"
                    id = "23df2280-1679-4eef-af4d-2d5b16f3941c"
                    tasks = "app:clean"

                    executionMode = BuildStep.ExecutionMode.ALWAYS
                    gradleParams = "--stacktrace --no-build-cache"
                }
                gradle {
                    buildFile = "build.gradle.kts"
                    enabled = true
                    name = "DoesnExistBuild2"

                    id = "23df2280-1679-4e9f-af4d-2d5b16f3941c"
                    tasks = "doesntExistTask"
                    executionMode = BuildStep.ExecutionMode.ALWAYS
                    gradleParams = "--stacktrace --no-build-cache"

                    conditions {
                        doesNotExist("skip_tanker_checks")
                    }
                }
                gradle {
                    enabled = true
                    name = "Build"
                    // buildFile = "build.gradle.kts"
                    // useGradleWrapper = true

                    tasks = "app:build"
                    executionMode = BuildStep.ExecutionMode.RUN_ON_SUCCESS
                }
            }
        }
    }
}

object HttpsGithubComHiraevPublicTeamcitySettingsGit : GitVcsRoot({
    name = "TeamCity Settings"
    url = "https://github.com/Hiraev/public-teamcity-settings.git"
})

object TheGuardianNewsVcs : GitVcsRoot({
    name = "TheGuardianNews"
    url = "https://github.com/Hiraev/TheGuardianNews.git"
})
