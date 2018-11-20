# [Ƶero](https://github.com/zerocurrencycoin/Zero) Desktop GUI Wallet

## Graphical user interface wrapper for the [Ƶero](https://github.com/zerocurrencycoin/Zero) command line tools

This program provides a Graphical User Interface (GUI) for the Ƶero client tools that acts as a wrapper and
presents the information in a user-friendly manner.

![Screenshot](https://github.com/zerocurrencycoin/Zero-Java-Swing-Desktop-Wallet/raw/master/docs/ZeroWallet.png "Main Window")


## Building, installing and running the Wallet GUI

Before installing the Desktop GUI Wallet you need to have Ƶero up and running. The following [guide](https://github.com/zerocurrency/zero#building) explains how to set up [Ƶero](https://github.com/zerocurrency/zero).

**For security reasons it is recommended to always build the GUI wallet program from GitHub**
**[source](https://github.com/zerocurrencycoin/Zero-SwingWallet-Windows-MAC/releases/tag/0.76).**
The details of how to build it are described below (easy to follow).

1. Operating system and tools

   As of November 2018 (Ƶero v2.0.1) this program is mostly tested on Linux. The Linux tools you need
   to build and run the Wallet GUI are Git, Java (JDK7 or later) and Ant. If using Ubuntu Linux,
   they may be installed via command:
   ```
   user@ubuntu:~/build-dir$ sudo apt-get install git default-jdk ant
   ```
   For RedHat/CentOS/Fedora-type Linux systems the command is (like):
   ```
   user@centos:~/build-dir$ sudo yum install java-1.8.0-openjdk git ant
   ```
   The name of the JDK package (`java-1.8.0-openjdk`) may vary depending on the Linux system, so you need to
   check it, if name `java-1.8.0-openjdk` is not accepted.
   If you have some other Linux distribution, please check your relevant documentation on installing Git,
   JDK and Ant. The commands `git`, `java`, `javac` and `ant` need to be startable from command line
   before proceeding with build.

2. Building from source code

   As a start you need to clone the zero-swing-wallet-ui Git repository:
   ```
   user@ubuntu:~/build-dir$ git clone https://github.com/vaklinov/zero-swing-wallet-ui.git
   ```
   Change the current directory:
   ```
   user@ubuntu:~/build-dir$ cd zero-swing-wallet-ui/
   ```
   Issue the build command:
   ```
   user@ubuntu:~/build-dir/zero-swing-wallet-ui$ ant -buildfile ./src/build/build.xml
   ```
   This takes a few seconds and when it finishes, it builds a JAR file `./build/jars/.jar`.
   You need to make this file executable:
   ```
   user@ubuntu:~/build-dir/zero-swing-wallet-ui$ chmod u+x ./build/jars/.jar
   ```
   At this point the build process is finished the built GUI wallet program is the JAR
   file `./build/jars/.jar`

3. Installing the built Zero GUI wallet

  3.1. If you have built Zero from source code:

   Assuming you have already built it from source code in directory `/home/user/zero/src` (for
   example - this is the typical build dir. for Zero v1.0.6) which contains the command line tools `zero-cli`
   and `zerod` you need to take the created file `./build/jars/.jar` and copy it
   to directory `/home/user/zero/src` (the same dir. that contains `zero-cli` and `zerod`). Example copy command:
   ```
   user@ubuntu:~/build-dir/zero-swing-wallet-ui$ cp ./build/jars/.jar /home/user/zero/src    
   ```

4. Running the installed Zero GUI wallet

   Before running the GUI you need to start zerod (e.g. `zerod --daemon`). The wallet GUI is a Java program packaged
   as an executable JAR file. It may be run from command line or started from another GUI tool (e.g. file manager).
   Assuming you have already installed Zero and the GUI Wallet `.jar` in
   directory `/home/user/zero/src` one way to run it from command line is:
   ```
   user@ubuntu:~/build-dir/zero-swing-wallet-ui$ java -jar /home/user/zero/src/ZEROSwingWallet.jar
   ```
   If you are using Ubuntu (or similar ;) Linux you may instead just use the file manager and
   right-click on the `.jar` file and choose the option "Open with OpenJDK 8 Runtime".
   This will start the Zero GUI wallet.


### License
This program is distributed under an [MIT License](https://github.com/vaklinov/zero-swing-wallet-ui/raw/master/LICENSE).

### Disclaimer

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
