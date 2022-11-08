import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessExtensionPredeclare

buildscript {
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.3.1" apply false
    id("com.android.library") version "7.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false
    id("com.diffplug.spotless") version "6.11.0"
}

subprojects {
    afterEvaluate {
        apply(plugin = "com.diffplug.spotless")

        configure<SpotlessExtension> {
            kotlin {
                target("**/*.kt")
                targetExclude("$buildDir/**/*.kt", "bin/**/*.kt")
                ktlint("0.47.1")
                    /**
                     * Spotless does not (yet) respect the `.editorconfig` file, see:
                     * https://github.com/diffplug/spotless/issues/142
                     */
                    .editorConfigOverride(
                        mapOf(
                            "ij_kotlin_allow_trailing_comma" to true,
                            "ij_kotlin_allow_trailing_comma_on_call_site" to true,
                        )
                    )
                trimTrailingWhitespace()
                endWithNewline()
            }
            format("misc") {
                target("**/*.kts", "**/*.gradle", "**/*.xml", "**/*.md", "**/.gitignore")
                targetExclude("**/build/**/*.kts", "**/build/**/*.xml")
                trimTrailingWhitespace()
                indentWithSpaces(4)
                endWithNewline()
            }
        }
    }
}

configure<SpotlessExtension> {
    predeclareDeps()
}

configure<SpotlessExtensionPredeclare> {
    kotlin {
        ktlint("0.47.1")
    }
}
