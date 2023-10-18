# Minecraft Inventory Tracker #

Minecraft Inventory Tracker, or M-IT, is a Minecraft utility module based in forge. While the name is discriptive enough, M-IT is intended to track across multiple accounts and provide basic statistics about not only inventory, but also usage.

Would you like to know which blocks you use most? What blocks haven't you had yet? Which character has that amazing wooden sword?  M-IT should be able to tell you.

M-IT uses web calls to triggered by events to publish character usage and contents which is viewable in a web portal.  While the portal is hosted, the mod supports local installations and the website is available for download.

## Published Events

### Actions
Current actions which are events
* OnBlockPlace - When a player places a block, it is logged.
* OnBlockUse - Use a Potion or a firework? It's logged.
* OnBlockBreak - Breaking blocks in block game? It's logged.

### Storage
Types of storage, which when opened, are logged.
* Chest
* Enderchest
* Furance
* [Applied Energistics](https://appliedenergistics.org/)


## Goals

* Player Events and Inventory contents of multiple types are stored to an external endpoint.
* Users can access data website with Microsoft SSO for automatic character association.
* Users can associate additional characters to their account.
* Statistics are shown about block usage and locations.

