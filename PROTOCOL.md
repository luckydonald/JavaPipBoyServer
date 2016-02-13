| **Table of Contents**                                            |
| ---------------------------------------------------------------- |
| `1` [***Discover***](#discover)                                  |
| `1.1` [PC & PS4](#pc--ps4)                                       |
| `1.2` [XBox One](#xbox-one)                                      |
| `2` [***Communication***](#communication)                        |
| `2.1` [**Data Stream Format**](#data-stream-format)              |
| `2.2` [**Message Types**](#message-types)                        |
| `2.2.0` [Type 0: Heartbeat](#type-0-heartbeat)                   |
| `2.2.1` [Type 1: New Connection](#type-1-new-connection)         |
| `2.2.2` [Type 2: Busy](#type-2-busy)                             |
| `2.2.3` [Type 3: Data Update](#type-3-data-update)               |
| `2.2.4` [Type 4: Local Map Update](#type-4-local-map-update)     |
| `2.2.5` [Type 5: Command Request](#type-5-command-request)       |
| `2.2.6` [Type 6: Command Response](#type-6-command-response)     |
| `3` [***Sources***](#sources)                                    |

# Discover

### PC & PS4

The app send following broadcast packet to **UDP/28000**
```json
{"cmd": "autodiscover"}
```
It send receives a packet containing Information about the game.
The machine type is either **`PC`** or **`PS4`**.
```json
{"IsBusy": false, "MachineType": "PC"}
```

### XBox One

The app sends a 16 byte broadcast packet to port **UDP/5050**
using protocol called Smartglass. (Can anyone explain me the protocol? Its XBox standard.)

# Communication
Once Discovery has completed, the app connects to the server on Port TCP/27000.

Example of connecting to TCP/27000 on a PS4 with IP 192.168.0.101 with netcat:

```
$ nc 192.168.0.101 27000
```

## Data Stream Format

Data is streamed via the TCP connection, bi-directionally, between the server and the app. All data is [little-endian](https://en.wikipedia.org/wiki/Endianness).

The stream is composed of individual Messages of the form:

```C
struct Message {
  uint32_t size,
  uint8_t type,
  uint8_t content[size]
}
```

Basic format example:

```
0A 00 00 00 03 48 45 4C 4C 4F 57 4F 52 4C 44
```

| size(32) | type(8) | content(size)           |
|----------|---------|-------------------------|
| 0A000000 | 03      | 48454C4C4F574F524C44    |
| 10       | 3       | HELLOWORLD              |

### Message Types
#### Type 0: Heartbeat

The app will periodically send a "heartbeat" packet of type 0 and no content. In other words, length 0 and type 0.
The server replies with the same empty 5 bytes. The app will disconnect after 5 sends with incorrect/missing answer.

e.x.

```
00 00 00 00 00
```

| size(32) | type(8) | content(size) |
|----------|---------|---------------|
| 00000000 | 00      |               |

When the app receives a heartbeat, the app must send the same heartbeat (5 bytes of zeros) back to let the server know that the app is still running. If the app does not respond with a heartbeat, the server will close the TCP connection.

Heartbeats should be sent by the app only in response to the server.

#### Type 1: New Connection

Messages of type 1 are sent when the app first connects to the server. It contains a JSON string with the language and version of the game.

```JSON
{"lang": "de", "version": "1.1.30.0"}
```

The App presumably verifies version & lang, then replies with a heartbeat.
#### Type 2: Busy

Messages of type 2 are sent when the server is busy. A server will be busy if a Pipboy Companion app is already connected. The message contains no data.

e.x.
```
00 00 00 00 02
```

| size(32) | type(8) | content(size) |
|----------|---------|---------------|
| 00000000 | 02      |               |

#### Type 3: Data Update

Messages of type 3 contain updates to the database.
The database is a collection of values where each value is represented by an ID.

A Data Update payload is a sequence of data updates. Each update begins with a header:

```C
struct UpdateHeader {
  uint8_t type,
  uint32_t id
}
```

This is followed by data depending on the value type. The possible types are as follows:

| Type ID | Type     | Java      | Python    | Bytes           | Format of update data                                                                    |
| ------- | -------- | --------- | --------- | --------------- | ---------------------------------------------------------------------------------------- |
| 0       | BOOLEAN  | `boolean` | `bool`    |   1             | `uint8_t` 0: false 1: true. , value is true if data is non-zero.                         |
| 1       | INT8     | `byte`    | `byte`    |   1             | `int8_t` \[0x00-0xFF\]                                                                   |
| 2       | UINT8    | -         | (?)       |   1             | `uint8_t` \[0-255\]                                                                      |
| 3       | INT32    | `int`     | `int`     |   4             | `int32_t` \[0x00000000-0xFFFFFFFF\]                                                      |
| 4       | UINT32   | -         | (?)       |   4             | `uint32_t` \[0-4294967295\]                                                              |
| 5       | FLOAT    | `float`   | `float`   |   4             | `float32_t`                                                                              |
| 6       | STRING   | `String`  | `str`     |   n             | Null(`0x00`)-termined byte string. Encoding?                                             |
| 7       | ARRAY    | `[]`      | `list`    | 2+(n*4)         | `uint16_t length, uint32_t ids[length]`, ids are the value IDs of previously sent values |
| 8       | OBJECT   | `Hashmap` | `dict  `  | 2+(i*4)+2+(d*4) | See picture/text below                                                                   |


![fallout4](https://cloud.githubusercontent.com/assets/2737108/12401272/a91e8db2-be25-11e5-85f5-533f6e7f4006.png)


Objects are complex. There are two parts - a set of (key, value) pairs to add, followed by a set of old values to remove.
The first time an object is sent, the remove set will be empty.

The first part, (key, value) pairs to add, begins with a `uint16_t length`,
followed by `length` lots of (`uint32_t id`, `0x00`-terminated byte string `key`).
This maps `key` to the previously sent value with ID `id`.

The second part is `uint16_t length, uint32_t ids[length]`, where `ids` contains the IDs of values
which the object currently maps to. Any (key, value) pair which maps to such a value should be removed.

Note that it is possible to contain a removal and an addition for the same key in one update, for example
if the update said to add the (key, value) pair ("foo", 1234) and remove the value which "foo" currently maps to.
In this case, the new value replaces the old value.

Objects are unordered and keys will not be repeated.

###### Example
(read from top to down, left to right. Remember, data is [little-endian](https://en.wikipedia.org/wiki/Endianness#Calculation_order).)

| Data Update Attributes | example (bytes) | Interpretation                                       |
| -----------            | ------------    | --------------                                       |
| size                   | 3b000000        | `59` bytes content, size of packages (next table) included |
| type                   | 03              | Type 3: Data Update                                  |
| content                | (`59` bytes)    | **See next Table**                                   |


The content can contain many Data Packages.
They just follow after each other. 

| Data Item          | Attributes   | Data Attributes | example (bytes) | Interpretation |
| ------------------ | ------------ | --------------- | --------------- | -------------- |
| **First Package**  | type         |                 | 03              | Data Type 3: `INT32` |
|                    | id           |                 | 0a000000        | ID: 10 |
|                    | data         |                 | 2a000000        | Value: 42 |
|                    |              |                 |                 |  |
| **Second Package** | type         |                 | 07              | Data Type 7: Array |
|                    | id           |                 | 0b000000        | ID: 11 |
|                    | data         | length          | 0200            | Length 2: Array has 2 entries |
|                    |              | `id #1`         | 01000000        | ID of element 1: >>1 |
|                    |              | `id #2`         | 02000000        | ID of element 2: >>2 |
|                    |              |                 |                 |  |
| **Third Package**  | type         |                 | 08              | Data Type 8:  |
|                    | id           |                 | 0c000000        | ID: 12 |
|                    | added        | length          | 0200            | 2 elements to insert |
|                    |              | `id  #1`        | 05000000        | ID  of element 1: >>5 |
|                    |              | `key #1`        | 666f6f00        | Key of element 1: `"foo\0"` |
|                    |              | `id  #2`        | 06000000        | ID  of element 2: >>6 |
|                    |              | `key #2`        | 68656c6c6f00    | Key of element 2: `"hello\0"` |
|                    | removed      | length          | 0200            | 2 elements to remove |
|                    |              | first id        | 03000000        | remove element with ID >>3 |
|                    |              | second id       | 04000000        | remove element with ID >>4 |
 
corresponds to an update that:
* sets value with id `10` to be a `uint32` equal to `42`
* sets value with id `11` to be an array containing the values with ids `1, 2`
* updates value with id `12`, an object, to add keys `"foo": 5, "hello": 6` and remove values `3, 4`
Total size `59 bytes` is valid. *1 + 4 + 4 + 1 + 4 + 2 + 4 + 4 + 1 + 4 + 2 + 4 + 4 + 4 + 6 + 2 + 4 + 4* = *59*

#### Type 4: Local Map Update

Messages of type 4 contain binary image data of the current local map if you view the local map in the app.

```
struct Extend {
  float32_t x,
  float32_t y
}

struct Map {
      uint32_t width,
      uint32_t height,
      Extend nw,
      Extend ne,
      Extend sw,
      uint8_t pixel[ width * height ]
}
```

#### Type 5: Command Request

Messages of type 5 are sent by the app to the server to request an action be taken in the game. The content of the message is a JSON string of the form:

```JSON
{"type": 1, "args": [4207600675, 7, 494, [0, 1]], "id": 3}
```

 * The command type (type) is seen in range of 0 to 14
 * The arguments to the command (args) differ based on the type property
 * The id of the command increments with every command send.

|  Command Type  |  Args                                                         |  Comment                                                                                                            |
| -------------- | ------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------- |
|  0             |  `[ <HandleId>, 0, <$.Inventory.Version> ]`                   |  Use an instance of item specified by `<HandleId>`                                                                  |
|  1             |  `[ <HandleId>, <count>, <$.Inventory.Version>, <StackID> ]`  |  Drop `<count>` instances of item, `<StackID>` is the whole list under `StackID`                                    |
|  2             |  `[<HandleId>, <StackID>, <position>, <$.Inventory.Version>]` |  Put item on favorite `<position>` counts from far left 0 to right 5, and north 6 to south 11                       |
|  3             |  `[<ComponentFormId>, <$.Inventory.Version>]`                 |  Toggle *Tag for search* on component specified by `<ComponentFormId>`                                              |
|  4             |  `[<page>]`                                                   |  Cycle through search mode on inventory page ( 0: Weapons, 1: Apparel, 2: Aid, 3: Misc, 4: Junk, 5: Mods, 6: Ammo ) |
|  5             |  `[<QuestId>, ??, ??]`                                        |  Toggle marker for quest                                                                                            |
|  6             |  `[ <x>, <y>, false ]`                                        |  Place custom marker at `<x>,<y>`                                                                                   |
|  7             |  `[]`                                                         |  remove custom marker                                                                                               |
|  8             |   |                                                           |                                                                                                                     |
|  9             |  `[<id>]`                                                     |  Fast travel to location with index `<id>` in database                                                              |
|  10            |   |                                                           |                                                                                                                     |
|  11            |   |                                                           |                                                                                                                     |
|  12            |  `[<id>]`                                                     |  Toggle radio with index `<id>` in database                                                                         |
|  13            |  `[]`                                                         |  Toggle receiving of local map update                                                                               |
|  14            |  `[]`                                                         |  Refresh? Command with no result. Issued when tab is changed.                                                       |

##### Command 1: Drop item

Example:
```json
{"type":1,"args":[4207600413,1,0,[0]],"id":56}
```

Drop the item at index `0`, a fedora (amount: `1`), inside the apparel page (page index `1`)  

##### Command 6: Set way point

Example:
```json
{"type":6,"args":[-71774.303255814,87841.2072351421,false],"id":11}
```

##### Command 9: Fast travel

Example:
```json
{"type":9,"args":[48363],"id":15}
```

##### Command 12: Toggle a radio station

Example:
```json
{"type":12,"args":[50308],"id":34}
```

Known station ids:   
             
| ID    | Name         |
| ----- | ------------ | 
| 50313 | Diamond City |
| 50308 | Classical    |

##### Command 13: Toggle local map view

Example:
```json
{"type":13,"args":[],"id":18}
```

#### Type 6: Command Response

Messages of type 6 are responses to commands. Currently, it appears that responses are only received for commands of type 9.

```JSON
{"allowed":true,"id":3,"success":true}
```

# Sources
 - [mattbaker/pipboyspec/communication.md PR#1](https://github.com/ekimekim/pipboyspec/blob/data-update-format/communication.md)
 - [luckydonald/d128fe05acdfff76d8be (gist)](https://gist.github.com/luckydonald/d128fe05acdfff76d8be)
 - [NimVek/pipboy/PROTOCOL.md](https://github.com/NimVek/pipboy/blob/master/PROTOCOL.md)
 - [Gavitron/pipulator/captures/notes.txt](https://github.com/Gavitron/pipulator/blob/33d0b9ecfedcfe0e1351be1cd16918e6336e3fdb/captures/notes.txt)
 - [RobCoIndustries/pipboylib/docs/app-msg-spec.md](https://github.com/RobCoIndustries/pipboylib/edit/master/docs/app-msg-spec.md)
 - [luckydonald/JavaPipBoyServer/PROTOCOL.md](https://github.com/luckydonald/JavaPipBoyServer/blob/master/PROTOCOL.md)