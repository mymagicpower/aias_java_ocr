### 官网：
[官网链接](https://www.aias.top/)

#### 下载模型，放置于models目录
- 链接：https://pan.baidu.com/s/1DOlDnpxf8XUvTGfFrCWdmQ?pwd=gdin

## 文字识别（OCR）工具箱
文字识别（OCR）目前在多个行业中得到了广泛应用，比如金融行业的单据识别输入，餐饮行业中的发票识别，
交通领域的车票识别，企业中各种表单识别，以及日常工作生活中常用的身份证，驾驶证，护照识别等等。
OCR（文字识别）是目前常用的一种AI能力。

#### 图像方向检测与旋转SDK

##### 1. 方向检测 - DirectionDetExample
模型本身支持 0 度，和 180 度两种方向分类。
但是由于中文的书写习惯，根据宽高比可以判断文本的90度和270度两个方向。
- 0度
- 90度
- 180度
- 270度   
![OcrDirectionExample](https://aias-home.oss-cn-beijing.aliyuncs.com/AIAS/OCR/images/OcrDirectionExample.jpeg)

##### 2. 方向旋转 - RotationExample
- 逆时针旋转
- 每次旋转90度的倍数
  ![RotationExample](https://aias-home.oss-cn-beijing.aliyuncs.com/AIAS/OCR/images/RotationExample.jpeg)



### 开源算法
#### 1. sdk使用的开源算法
- [PaddleOCR](https://github.com/PaddlePaddle/PaddleOCR)

#### 2. 模型如何导出 ?
(readme.md 里提供了推理模型的下载链接)
- [export_model](https://github.com/PaddlePaddle/PaddleOCR/blob/release%2F2.5/tools/export_model.py)
- [how_to_create_paddlepaddle_model](http://docs.djl.ai/docs/paddlepaddle/how_to_create_paddlepaddle_model_zh.html)



#### 帮助文档：
- https://aias.top/guides.html
- 1.性能优化常见问题:
- https://aias.top/AIAS/guides/performance.html
- 2.引擎配置（包括CPU，GPU在线自动加载，及本地配置）:
- https://aias.top/AIAS/guides/engine_config.html
- 3.模型加载方式（在线自动加载，及本地配置）:
- https://aias.top/AIAS/guides/load_model.html
- 4.Windows环境常见问题:
- https://aias.top/AIAS/guides/windows.html