# Car-Damage-Detection-YOLOV3
Dents and Scratch Detection on car using YOLOV3 trained on Darknet-53

```sh
$ git clone https://github.com/aayush2710/Car-Damage-Detection-YOLOV3.git
$ git-lfs pull
$ pip install -r requirements.txt
```
## To Train
```sh
$ cd Train
$ python Train.py ---help
$ python Train.py --pretrained_weights PretrainedWeights/darknet53.conv.74 #Train using default settings
```
## To Detect
```sh
$ cd Detect
$ python Detect.py ---help
$ python Detect.py --image_folder ='Folder Containing Images'
```
## On Android Mobile Device
[Download APK](https://github.com/aayush2710/Car-Damage-Detection-YOLOV3/releases/download/v1.0/app-release.apk)

## Credits
1. [Eriklindernoren's pytorch implementation of YoloV3](https://github.com/eriklindernoren/PyTorch-YOLOv3)
