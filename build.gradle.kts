plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("7.3.1").apply(false)
    id("com.android.library").version("7.3.1").apply(false)
    kotlin("android").version("1.7.10").apply(false)
    kotlin("multiplatform").version("1.7.10").apply(false)

}
buildscript {
     repositories {
         gradlePluginPortal()
         google()
         mavenCentral()
     }
     dependencies {
         classpath("io.realm.kotlin:gradle-plugin:1.4.0")
     }
 }

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
