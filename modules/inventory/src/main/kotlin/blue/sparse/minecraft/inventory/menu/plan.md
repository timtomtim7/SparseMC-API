# Classes
## Menu
* A menu represents a list of elements.
* Adding an element warns about overlapping/collisions/intersection with other elements

## Element
* An element has:
  * 2D Position
  * 2D Size 
* Examples of an element type might include:
  * EditableArea
  * Static
  * Button
  * TabbedPane

# Examples
## Basics
Each `#` represents an item in a menu
```
# # # # # # # # #
# # # # # # # # #
# # # # # # # # #
```

## Example 1
```
menu(rows = 3) {
	title("locale.key.here", "placeholders" to "here")
	val area = element(type = EditableArea, position = 0 x 0, size = 4 x 3)
	element(type = Static, position = 4 x 0, size = 1 x 3) {
		icon(Material.IRON_FENCE)
		name("locale.key.here", "placeholders" to "here")
	}
}
```
```
! ! ! ! & # # # #
! ! ! ! & # # # #
! ! ! ! & # # # #
```
Everywhere that is a `!` would have no item and would be editable,
i.e. a player could add or remove items there

Everywhere that is a `&` would not be editable by any player and would
default to iron bars.

## Example 2
```
menu(rows = 6) {
	title("locale.key.here", "placeholders" to "here")
	element(TabbedPane, position = 1 x 0, size = 4 x 6) {
		tabPosition = TabPosition.TOP
		defaultTab = "idHereA"
		
		addTab("idHereA", "locale.key.here", "placeholders" to "here") {
			
		}
		addTab("idHereB", "locale.key.here", "placeholders" to "here") {
        	tabPosition = 5
        	
        	onSwitch { toTabID: String -> 
        	
        	}
        	onClose {
            		
			}
		}
	}
	
	onClose {
		
	}
}
```

```
# A B # # # # # #
# - - - - # # # #
# # # # # # # # #
# # # # # # # # #
# # # # # # # # #
# # # # # # # # #
```