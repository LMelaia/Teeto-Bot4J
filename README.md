# Teeto-Bot4J
Java port for the C# implementation of Teeto-Bot. This implementation is designed to be run
on a linux server as well as continue the development of Teeto-Bot.

Teeto-Bot is a personal bot designed to improve the experience of a Discord server shared between friends.
It will continue to provide admin, voice, information and various other utilities as well
as provide a way to modify Discord to suit personal needs. Not to mention jokes.

## Main Goal
The main goal of Teeto-Bot is to be an always-on bot capable of running on Windows and Linux
while using minimal system resources and still providing useful features.

## Using the bot
### Token
The bot requires a token to login.
This token is provided by discord when
creating the bot.

The token must be placed in
a file named `.TOKEN` in the projects root directory,
either the parent directory of /bin in a release
environment or the projects root folder
in a development environment.

The `.TOKEN` file is ignored by git.

On Windows, the file can be created
using the command `type NUL > .TOKEN`.
Explorer does not allow creating
a `.name` file.

### Running the bot.
With the token file in place. The bot is ready
to run using the standard gradle task `:run`.

## License
GNU General Public License V3.0 (GNU GPL 3.0)

## Important
Teeto-Bot is a *personal/private* bot for a *personal/private* server. It provides **no** guarantees to
work as intented. The code is published here for version control, backups and to make the code
public in the hopes it may prove useful to someone. 
