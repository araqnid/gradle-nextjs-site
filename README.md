# Next.JS site export from Gradle

This plugin allows you to create a React site based on Next.JS embedded within a Gradle project. During build,
`next export` will be used to produce a ZIP file of the site which could then be published or embedded into a JAR
in a related project.

## Example usage

In `build.gradle.kts`:

```kotlin
plugins {
  id("org.araqnid.nextjs-site") version ("0.0.1")
}

nextJsSite {
  // defaults
  debugBuild.set(false)
  productionProfiling.set(false)
  lint.set(true)
}
```

This plugin will use `yarn` to install dependencies and run `next export`. You should
supply a `package.json`, `next.config.js` etc.

This plugin expects that your source is in a `src` directory.

The following tasks are added:

- `nextBuild` runs `next build`
- `jestTest` runs `jest --ci` and configures the `jest-junit` reporter
  so that Gradle will collect the test output
- `nextExport` runs `next export`

`nextExport` is added as a dependency to `assemble`, and `jestTest`
is added as a dependency to `check`.

The exported site will be written to `build/site` and is advertised as a `web` configuration
of the project.

## Configuration

This plugin defines a `nextJsSite` extension to receive settings. Most of the settings
correspond to options to pass to `next export`.

### debugBuild

### productionProfiling

### lint
