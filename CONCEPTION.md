### _CHANGES MADE TO DEFAULT FILES_

#### `Orientation.java`

	- getCode(Orientation orientation)
	
Used in `ARPGPlayer.java` :	
We added this method because we were having a bug letting the player
orientate when it would not be natural to do so. If the player was
facing a wall and holding down the key in the wall direction (f. ex.
the left arrow when facing a wall to the left) and pressed another
arrow key (f.ex. up) he would orientate to that direction without
moving. By adding this method, it is now impossible to do so, you
are stuck in the direction of the wall and you cannot orientate.

#### `Area.java`

	- registerActors(Actor[] a)
Used in `Road.java` :
This method is useful if we want to register a lot of actors at once.
We used it in Road.java to spawn a full region of grass at once,
combined with the static method grassZone in Grass.

#### `RPGSprite.java`

Every method added in RPGSprite.java is used to facilitate the
animations.
	
	- extractSprites(String name, int nbFrames, float width, float height, Positionable parent, int regionWidth, int regionHeight)
Used in `Coin.java`, `FireSpell.java`, `Grass.java`, `Heart.java`,` MagicWaterProjectile.java`, `Waterfall.java` :
This method is useful when the entity linked to the sprites does not
have a specific orientation (or at least, the sprite does not change
even if the orientation does). Each of the classes that use this
method have their orientation independent of the sprites.

	- extractSprites(String name, int nbFrames, float width, float height, Positionable parent, int heightOfRegion, int regionWidth, int regionHeight)
Used in `Orb.java` :
This method is useful if the sprites we want is not at the top of the
image. It allows us to choose the height of the region.

	- extractSprites(String name, int nbFrames, float width, float height, Positionable parent, int regionWidth, int regionHeight, Vector anchor)
Used in `Bomb.java`, `Monster.java` :
This method is basically the same as the first one except we can add
an anchor argument.

	- extractVerticalSprites(String name, int nbFrames, float width, float height, Positionable parent, int regionWidth, int regionHeight, Vector anchor)	
Used in `LogMonster.java` :
This method is basically the same as the third one except it extracts
the sprites vertically and not horizontally.

#### `SwingWindow.java`

	- setCloseRequested()
Used in `ARPG.java` :
We want to let the player decide at one point if he wants to quit the game.
By closing the window, it quits the game (when he dies or when
he saves the king).

#### `Window.java`
	- setCloseRequested()
Overridden in `SwingWindow.java`, used in `ARPG.java` :
We could have another type of window and we still want that window to
give the possibility to close it so we put this method in window.java.
The class that implements it in our project is SwingWindow.java so we
simply overrode this method, as explained above.

### _EXTENSIONS_

The new features added to the game are :

	- Animated Waterfall in Road
	- The player can fly if you have wings
	- The player can sprint (or fly faster) by pressing Q
	- Two new areas : RoadTemple and Temple
	- By hitting the orb with an orb you can spawn a bridge
	- Chest in Temple
	- Entities can be invincible
	- Shop in Village
	- Death animation and meet with God if you die/if you save the king.

### _NEW CLASSES AND INTERFACES_

You will find below each new class that we created. A brief description of the
class is given if it is part of an extension.

In `arpg` :

	- ARPG.java
	- ARPGBehavior.java
	- ARPGItem.java

In `arpg/actor` :

	- ARPGInventory.java
	- ARPGPlayer.java
	- ARPGPlayerStatusGUI.java
	- Arrow.java
	- Bomb.java
	- Bridge.java :
		Concrete realization of TriggerableEntity.java that modelizes the
		bridge found in Road that can be crossed only if the orb has been
		hit.
	- CastleDoor.java
	- CastleKey.java
	- Chest.java :
		Class representing the chest found in Temple that can be opened
		to receive items.
	- Coin.java
	- DarkLord.java
	- FireSpell.java
	- FlameSkull.java
	- God.java :
		Class representing God that you can find in paradise if you die or
		if you save the king. He is so kind that he will leave your items
		when you respawn, you won't need to find them back.
	- Grass.java
	- Heart.java
	- King.java :
		Class representing the king found in Castle which can be talked to
		to finish the game.
	- LogMonster.java
	- MagicWaterProjectile.java
	- Orb.java :
		Class representing the orb that can be hit and if so, trigger the
		bridge, letting you cross the river in Road.
	- Shop.java :
		Class representing the shop found in Village where the player can
		buy items.
	- Sword.java
	- Waterfall.java :
		Class representing a waterfall. We created this class to modelize
		the waterfall in Road.
	- WhiteHalo.java :
		We created this class to bring a halo that fades over time when
		the player respawns or when he gets to paradise.

In `arpg/area` :

	- ARPGArea.java
	- Castle.java
	- Farm.java
	- Paradise.java
	- Road.java
	- RoadCastle.java
	- RoadTemple.java
	- Temple.java
	- Village.java

In `arpg/handler` :

	- ARPGInteractionVisitor.java

In `rpg` :

	- InventoryItem.java
	- RPG.java

In `rpg/actor` :

	- CollectableAreaEntity.java
	- FlyableEntity.java
	- Inventory.java
	- InvincibleEntity.java :
		Interface representing an entity that can be invincible. Each
		subclasses will have to redefine the method isInvincible() to
		know when they are invincible.
	- Monster.java
	- Projectile.java
	- TriggerableEntity.java :
		Abstract class representing an area entity that can be triggered
		(and can have different behaviors depending on if it has actually
		been triggered). We added this method to generalize the Bridge
		class to an entity that can be triggered.		
	- Weapon.java :
		Abstract class representing a weapon. A weapon can be registered
		on a cell and will want a cell interaction.

### _CHOICES OF PACKAGES_

Most of our classes have been put in the arpg/actor package. Each class that is
not specific to an ARPG game has been put in the rpg or rpg/actor package. An
inventory item is not considered to be an actor because it does not update over
time and has been put in the rpg package. Every other non-ARPG class is in
rpg/actor. For the same reason, ARPGItem is in the arpg package and every other
class is in arpg/actor except the areas and the ARPG handler which have been
put in their packages respectively.


### _DIFFERENCES WITH THE INSTRUCTIONS_

We did everything as the guideline told us to do except for the health system.
Instead of representing each health point with a heart, we represented each
health point with a half-heart, that means that a heart represents two health
points. No difference is seen in-game, but we can manipulate integers instead
of floats in our code as a half-heart is 1 health point instead of 1/2.

We also did not create an ARPGCollectableAreaEntity class as the guideline would suggest : 

    "Dans votre jeu ARPG, un objet ramassable aura une déclinaison spécifique: il sera automatiquement ramassé lorsqu’on lui marche dessus."
    
because we did not find necessary to create a class only for this reason.