![Wheel - "When all you do feels repetitive"](https://cdn.modrinth.com/data/cached_images/e893942d30a3b6161abc5c4b0f67ee12ea38d39f.png)
# Summary

Client-side minecraft mod that allows you to add your own
small reusable pieces of JSON "code" via resource packs, or
download already made ones.

# Creating your own widgets

Start off by creating a resource
pack, where all your widgets will be located at. Inside your namespace,
create a folder named `widgets`.

```
root
├── assets
│   └── namespace
│       └── widgets
├── pack.mcmeta
└── pack.png
```

Inside this folder, create your first widget file, let's call it `my_widget.json`. 
A widget file represents an entry on the Wheel and consists of three main parts.
Them being: `label`, `preview`, and `actions` fields. We're going to discuss what
each field does based on this example widget below.

```json
{
  "label": "Example Widget",
  "preview": "minecraft:grass_block",
  "actions": [
    {
      "type": "echo",
      "message": "Hello, world!"
    }
  ]
}
```

You can already probably tell what those fields are used for, nevertheless, discussing them would
still clear some details, so let's begin.

> 💡 You can use the [widget JSON schema](https://raw.githubusercontent.com/Manchick0/Wheel/master/schema/widget.json) to simplify the process of creating new widgets.

## `label`  Field*

The label is the part that is shown to a player when they hover over the widget - the **Tooltip** of the
widget. Since `label` is a JSON text object, it can either be set to a string, or to a full object, such
as:

```json
{
  "text": "Example Widget",
  "color": "gold",
  "bold": true,
  "italic": true
}
```
## `preview` Field*

The preview represents an item that is displayed on the widget as its cover. Just like with the `label`,
this can either be set to a string, which will then represent the item's id, or to a full item stack object, such as:

```json
{
  "id": "minecraft:stone",
  "count": 1,
  "components": {
    "custom_model_data": 5
  }
}
```

## `actions` Field*

The `actions` field represents an array of actions that will be run when the widget
gets clicked. We'll discuss more about action types below, but the syntax stays pretty
simple, `"type"` points to an action type, whilst all the fields required by that type
are listed below. In our example:

```json
{
  "actions": [
    {
      "type": "echo",
      "message": "Hello, world!"
    }
  ]
}
```

## `take_slot` Field

Widgets don't take a strict spot on the wheel by default, but this can be
changed by setting the `take_slot` field to one of its values:

```
"top_left"       "top"      "top_right"

"left"                          "right" 

"bottom_left"  "bottom"  "bottom_right"
```

# Exploring Action Types

There are plenty of action types you can perform. Some of them are
used more often than others, some of them don't do anything of their own,
but ALL of them are listed below :)

> 💡 Fields annotated with [*] are required, all the other ones
> are completely optional.

## `echo`

Echo sends the specified message to the chat, or displays it in the actionbar.

### Fields:
* `message`: The message. **JSON-Text** [*]
* `overlay`: Whether to show in the actionbar instead. **Boolean**

## `command`

Runs the specified command.

### Fields:
* `command`: The command. If the first character is '/', it will be ignored. **String** [*]

## `open`

Lists all the widgets in the specified path and opens a new wheel with those widgets. The path is a sub path within the `widgets` folder in a resource pack.

### Fields:
* `path`: The path. Must match the following regex: "[a-zA-Z0-9_/]*". **String** [*]

## `sound`

Plays the specified sound to the player, or stops it instead.

### Fields:
* `id`: The identifier of the sound. **String**
* `volume`: The volume value. **Float**
* `pitch`: The pitch value. **Float**
* `stop`: Whether to stop the sound instead. If the `id` isn't specified, this will stop all sounds. **Boolean**

## `clipboard`

Copies the content to the clipboard of the player.

### Fields:
* `content`: The content to copy. **String** [*]

## `suggest`

Opens the chat with the specified text inserted.

### Fields:
* `content`: The content to suggest. **String** [*]
* `cusrsor`: If included, sets the cursor to the specified position. **Integer**

## `link`

Opens the specified URI.

### Fields:
* `destination`: A URI specifying the destination of the link. If set to ":run", opens the run directory instead. **String** [*]
* `ignore_confirmation`: Whether to skip the confirmation screen whilst opening the link. **Boolean**

## `await`

Performs the specified action(s) after a certain amount of ticks.

### Fields:
* `actions`: The action(s) to perform. Can either be set to an action, or an array of such. **Object | Array** [*]
* `delay`: The amount of ticks to wait before executing the actions. **Boolean** [*]

## `random`

Randomly selects one of the specified actions and executes it.
If only one single action is present, it has a 50% chance of executing it
instead.

### Fields:
* `actions`: The action(s) to perform. Can either be set to an action, or an array of such. **Object | Array** [*]