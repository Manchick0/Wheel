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
* `id`: The identifier of the sound. **Identifier**
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
* `actions`: The action(s) to perform. Can either be set to an action, or an array of such. **Action | Action Array** [*]
* `delay`: The amount of ticks to wait before executing the actions. **Integer** [*]

## `random`

Randomly selects one of the specified actions and executes it.
If only one single action is present, it's either executed, or not.

### Fields:
* `actions`: The action(s) to perform. Can either be set to an action, or an array of such. **Action | Action Array** [*]

## `value`

> Consider taking a look at [Working with Values](#working-with-values) for a comprehensive tutorial
> on how to use values together with conditions.

Operates the specified `value` and assigns it to the variable referred by the `id` field.
The value can either be directly provided or referred by an identifier.

### Fields:
* `id`: The identifier associated with the variable. **Identifier** [*]
* `operation`: The operation to perform on the value. Defaults to `assign` if not specified. **Operation**
* `value`: The value to use for the action. Can either be an identifier or a double value. **Identifier | Double** [*]

## `condition`

Evaluates the provided expression and executes either `if` actions, if the condition
was met, or `else` ones if it wasn't.

### Fields:

* `expression` The expression to check. **Expression** [*]
* `if` The action(s) to execute if the expression was evaluated to be `true`. **Action | Action List** [*]
* `else` The action(s) to execute if the expression was evaluated to be `false`. **Action | Action List**

# Working with Values

Values are a crucial part of any advanced widget pack. They have a ton of possible usages,
including:

- **Radio Buttons**: A good way to add some states to your widgets.
- **Configs**: Let the user choose a value out of pre-defined list,
and then make different choices based on that very variable.
- **Mini Games**: You can even push the possibilities to their boundaries,
and try to create a full game!

## Assigning a Value

Values are stored as identifiers within the config, meaning they are persistent and are accessible from anywhere in the game.
Setting a value involves calling the `value` action:

```json
{
  "type": "value",
  "id": "example:value",
  "value": 5
}
```

Just like that, we can assign the value with an identifier of `"example:value"` to 5. Now that we have an assigned
value, let's create another one:

```json
{
  "type": "value",
  "id": "example:another_value",
  "value": "example:value"
}
```

Right away, you might have noticed that we now use a string instead of a double. This is called **"dynamic values"**.
It's a way to use already assigned values throughout your actions, in places where they are supported.

A such place is.... `echo` action! To use a dynamic value in an echo action,
you can simply surround its identifier with such "quotes": `$()`

```json
{
  "type": "echo",
  "message": {
    "text": "My cool value: $(example:value)"
  },
  "overlay": true
}
```

This would output `My cool value: 5`, since we've assigned our `example:value` to 5.

### Using Operations

Up until now, we've only assigned a value to a set number. What if we had to use more complex **operations**?
That's where the optional `operation` comes in handy. It defaults to `assign`, which we've been using all along,
so it's time to specify an operation we **actually** want.

```json
{
  "type": "value",
  "operation": "sum",
  "id": "example:another_value",
  "value": "example:value"
}
```

We use the `sum` operation here. It adds two values together, and just like **all** other operations,
stores the result in the value specified by the `id` field. Of course, you aren't only limited to `assign`
and `sum` operations. In fact, there are plenty of other ones you can use:

<details>
<summary>View other Operations</summary>

* `assign` -  assigns the `id` to the `value`
* `sum` - assigns the `id` to the sum of `id` and `value`
* `difference` - assigns the `id` to the difference between `id` and `value`
* `product` - assigns the `id` to the product of `id` and `value`
* `quotient` - assigns the `id` to the quotient of `id` and `value`
* `remainder` - assigns the `id` to the remainder of `id` and `value`
* `power` - raises `id` to the power of `value`, and assigns it to the result.
* `sqrt` - calculates the square root of `value`, and assigns `id` to the result.
* `sine` - calculates the sine of `value`, and assigns `id` to the result.
* `cosine` - calculates the cosine of `value`, and assigns `id` to the result.
* `max` - assigns `id` to whatever is greater: `id` or `value`.
* `min` - assigns `id` to whatever is smaller: `id` or `value`.
* `random` - assigns `id` a random double between 0 and `value`.
* `round` - rounds `value` to the nearest whole number and assigns `id` to the result.
</details>

# Checking a Condition

Now that you're familiar with how values are assigned and stored, we can use `condition`s to execute
different actions based on `expressions`. 

Using a `condition` action is pretty simple. It consists of two main parts, an `expression`, which is a
properly structured string that is then used to determine whether the condition is true, and two action
lists - one of them is executed if the condition was met, the other one if it wasn't.

```json5
{
  "type": "condition",
  "expression": "5 > 2",
  "if": [
    /* ... */
  ],
  "else": [
    /* ... */
  ]
}
```

## How to Structure Expressions

In the example above, the `expression` is: "5 > 2", which **evaluates** to true, since 5
is always greater than 2. You might think it's pretty useless, we all knew that 5 is
greater than 2. Exactly, it is! What isn't useless, is using the very **dynamic values**
we've discussed earlier:

```json5
{
  "type": "condition",
  "expression": "$example:value > 2",
  "if": [
    /* ... */
  ],
  "else": [
    /* ... */
  ]
}
```

With this small adjustment, our expression is now not very clear. What is `$example:value`, is it 2, or is it 5?
If you remember, we did indeed assign `example:value` to 5, so the expression is true! But what if it wasn't? What
if `example:value` was set to 1? The expression would be false. 

Let's take a look at another example, this time a tiny bit more complex:

> 💡 You can use different operators to compare variables. Them being: '>', '<', and '='.

```json5
{
  actions: [
    {
      "type": "condition",
      "expression": "$example:click = 0",
      "if": [
        {
          "type": "echo",
          "message": "First click!"
        },
        {
          "type": "value",
          "id": "example:click",
          "value": 1
        }
      ],
      "else": [
        {
          "type": "echo",
          "message": "Some click!"
        }
      ]
    }
  ]
}
```

If the actions provided in this example were packed in a widget, clicking it would first output `First click!`, and then
`Some click!` on any further click. This is because the value `example:click` is changed after the condition is met, in fact, it
is also changed to a value that doesn't match the provided expression. That means that the condition can only evaluate to `true`
once.
