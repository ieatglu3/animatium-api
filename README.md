# Animatium API
Thread-safe Kotlin library for providing a server-side API access for the Animatium client mod.

---
## Usage

### Access Player Information

Retrieve and manage players using the player registry:

```java
import java.util.UUID;
import com.github.ieatglu3.animatiumapi.AnimatiumAPI;
import com.github.ieatglu3.animatiumapi.AnimatiumPlayer;
import java.util.Optional;

AnimatiumAPI api = AnimatiumAPI.get();
UUID playerUUID = ...;

// Get an optional player
Optional<AnimatiumPlayer> player = api.getPlayer(playerUUID);
if (player.isPresent()) {
  System.out.println("Player is using Animatium");
} else {
  System.out.println("Player is not using Animatium");
}

// Get or null
AnimatiumPlayer playerOrNull = api.getPlayerOrNull(playerUUID);

if (playerOrNull != null) {
  System.out.println("Player is using Animatium");
} else {
  System.out.println("Player is not using Animatium");
}

// Check if player has Animatium
if (api.hasAnimatiumInfo(playerUUID)) {
  System.out.println("Player is using Animatium");
} else {
  System.out.println("Player is not using Animatium");
}
```

### Manage Server Features

```java
import com.github.ieatglu3.animatiumapi.ServerFeature;
import com.github.ieatglu3.animatiumapi.AnimatiumAPI;
import com.github.ieatglu3.animatiumapi.AnimatiumPlayer;

AnimatiumAPI api = AnimatiumAPI.get();
AnimatiumPlayer player = api.getPlayerOrNull(playerUUID);

// Enable a single feature
player.enableFeature(ServerFeature.MissPenalty);

// Enable multiple features
player.enableFeatures(List.of(ServerFeature.OldSneakHeight, ServerFeature.ClientsideEntities));

// Check if a feature is enabled
if (player.isFeatureEnabled(ServerFeature.MissPenalty)) {
  System.out.println("Miss Penalty is enabled for this player");
}

Set<ServerFeature> enabled = player.enabledFeatures();

// Disable features
player.disableFeature(ServerFeature.MissPenalty);
player.disableFeatures(List.of(ServerFeature.OldSneakHeight));
player.disableAllFeatures();

// Set specific features (replaces all)
player.setEnabledFeatures(List.of(ServerFeature.ClientsideEntities));

for (AnimatiumPlayer player : api.players()) {
  for (ServerFeature feature : ServerFeature.all()) {
    if (player.isFeatureEnabled(feature)) {
      System.out.println("Player " + player.name() + " has " + feature.id() + " enabled");
    }
  }
}
```

---
### Available Server Features
See (https://github.com/Legacy-Visuals-Project/Animatium#feature) for available features.

---

### Installation
#### Option 1: Git Clone

Clone the repository and build locally:

```bash
git clone https://github.com/ieatglu3/animatium-api.git
cd animatium-api
./gradlew build
```

Then add it to your project's `build.gradle.kts`:

```kotlin
repositories {
  mavenLocal()
}

dependencies {
  implementation("com.github.ieatglu3:animatium-api:1.0.0")
  // or 
  implementation("com.github.ieatglu3:animatium-[platform]:1.0.0")
}
```

#### Option 2: JitPack

Add the JitPack repository to your `build.gradle.kts`:

```kotlin
repositories {
  maven("https://jitpack.io")
}

dependencies {
  implementation("com.github.ieatglu3:animatium-api:1.0.0") 
  // or
  implementation("com.github.ieatglu3:animatium-[platform]:1.0.0")
}
```