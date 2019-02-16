# Scripts to install on the Raspberry Pi

To deploy the application you need to follow the instructions of the [Installation document](../Installation.md)  on the main Github directory.

These scripts must be copied into the ```/root```  directory, then files  ```HomeControl.target```, ```ControlMonitor.service``` and ```Listener.service``` must be copied also to the ```/etc/systemd/system directory```

To enable and start the monitor you must type:

```
sudo systemctl enable HomeControl.target
sudo systemctl start HomeControl.target
```

To check if the service is running properly, type:

```
sudo systemctl status HomeControl.target
```