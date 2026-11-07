# Development dependencies

This repository does **not** redistribute third-party mod binaries.

To build locally, place the exact production jars used by this project in this folder:

- `refinedstorage-1.12.4.jar`
- `projecte-1.20.1-PE1.0.1.jar`

Then run:

```bash
./gradlew build
```

`build.gradle` uses `fg.deobf("local:refinedstorage:1.12.4")` and
`fg.deobf("local:projecte:1.20.1-PE1.0.1")`, so the file names must match the
coordinates above.

The jars are only compile-time/runtime development dependencies and are not
packed into the released mod jar. Please obtain them from the official ProjectE
and Refined Storage distribution pages and follow their licenses.
