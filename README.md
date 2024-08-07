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
Root
├── Assets
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

## `label`  Field

Label is the part that is shown to a player when they hover over the widget - the **Tooltip** of the
widget. Since `label` is a JSON text object,
