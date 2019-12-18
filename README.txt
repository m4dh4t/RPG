_______________________________❄_HOW_TO_LAUNCH_❄________________________________


		To launch the game simply run Play.java


__________________________________❄_CONTROLS_❄__________________________________


		UP : 	walk up
		DOWN : 	walk down
		RIGHT : walk right
		LEFT : 	walk left
		Q :	run (hold down Q while moving and you will run)
		E :	interact (talk to the shopper, open a door/chest...)
		SPACE :	use current item
		TAB :	switch item
		ENTER :	skip the current dialog
		W :	while shopping, makes you leave


_____________________________❄_ADDITIONAL_CONTROLS_❄____________________________

We added some additional controls if you want to make some tests with our
inventory, as mentioned in the instructions :

		R :	add an arrow
		T :	add a sword
		Y :	add a staff
		U :	add a bow
		I :	add a bomb
		O :	add a castle key
		P :	add wings
		K :	add a chest key

You will mostly need to add some space to the inventory to be able to carry the
items you add (second argument of the constructor of ARPGInventory which is called
in the player constructor, line 77 of class ARPGPlayer).

In RoadCastle.java, you have additional controls :

		L :	spawn a Log Monster
		S :	spawn a Flame Skull
		B :	spawn a Bomb


____________________________________❄_WINGS_❄___________________________________


	We added wings to the game and we wanted to give some precision
	concerning them. You can find them in the shop or in the chest
	in Temple. To use them, you need to hold down SPACE when equipped
	and you will be able to freely move anywhere on the map (except
	walls). Be careful ! If you land on water, the player will not be
	able to move if you do not use the wings. You can still press Q
	while flying to move faster.


____________________________________❄_AREAS_❄___________________________________

Farm :
	- You spawn there. Nothing very interesting except 2 log monsters
	that you can hit with your sword or your bomb.

Village :
	- You can find a shop there. Refer to the controls to know how to
	talk to the shopper and leave.
	- You can use the arrow keys to select an item to buy. The price
	is indicated in the bottom-right corner of the window. Your money
	is indicated in the bottom-left corner. The numbers under each
	article is the available number of each article. If you see that
	you cannot buy an item, do not worry ! It simply means that you
	do not have enough money or that you are too heavy. The best way
	to get lighter is to drop your bombs by pressing SPACE (bombs are
	the most heavy item).
	- To find money to spend in the shop, you can either kill log
	monsters or find coins in the grass in Road.

Road :
	- There is two log monsters that you can fight.
	- There is a zone of grass that you can cut with your sword or
	burn with your bomb. Beware ! Burning grass can hit you. Cutting
	grass can give you a coin or a heart.
	- At the right side of the area, you can find an orb. You can hit
	it with a bow (that you can buy in the shop) and you obviously also
	need arrows. This will summon a bridge and you will be able to cross
	the river and enter in the next area : RoadTemple.

RoadTemple :
	- Beware of the log monster !
	- You can enter the temple.

Temple :
	- There is a chest that you can open only if you have a chest key.
	You can find a chest key in the shop. The chest gives you the items
	only if you can carry them (if you are too heavy, only the weightiest
	items will be given to you and the remaining ones will stay in the
	chest. You can always come back and take them if you are lighter).

RoadCastle :
	- The great boss is here ! The dark lord is only vulnerable to
	magic so you need to get a staff if you want to defeat him.
	The staff can be found in the chest in Temple. No changes have
	been made to the dark lord comparing it to what it is asked to do.
	- You can press S to spawn a fire skull, B to spawn a bomb and
	L to spawn a log monster.
	- The dark lord spawns a castle key when he dies, which you can
	use to open the castle door and enter the castle.

Castle :
	- Save the king and finish the game !

Paradise :
	- If you die or if you save the king you will find yourself in
	paradise ! You can either restart the game or quit the game if
	you die, or you simply win the game if you saved the king.

____________________________________❄_IMAGES_❄___________________________________

We used 7 images that were not included in the default folders.
These were :
	- The chest sprite : https://lecodemv.github.io/leTBS/user-guide/entities.html#sprite-configuration
	- The chest key sprite : http://www.iconarchive.com/show/noto-emoji-objects-icons-by-google/62952-old-key-icon.html
	- Any other sprite/animation was hand-made (The flying animations, the death
	animation, the wings icon), the paradise and the god were also hand-made from
 	a model.