# Scripts to install on the Raspberry Pi

To deploy the application you need to follow the instructions of the [Installation document](../Installation.md)  on the main Github directory.

These scripts must be copied into the ```/root```  directory, then  ```HomeControl.service``` file must be copied to the ```/etc/systemd/system directory```

To enable and start the monitor you must type:

```
sudo systemctl enable HomeControl
sudo systemctl start HomeControl
```

To check if the service is running properly, type:

```
sudo systemctl status HomeControl
```