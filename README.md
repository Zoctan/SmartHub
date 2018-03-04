# 智能插座

基于 Material Design 和 MVP 开发模式的智能插座客户端

支持：Android 5.0 及以上

服务器代码在分支 api 中，由于消息传递跨移动端、服务器、云平台和硬件插座 4 大部分，所以一些提示功能稍微有些延迟。

# 功能

1. 支持扫码添加插座
2. 支持对插座的状态、日用电量查看、设置定时开关
4. 支持日夜间模式
5. 目前基本功能已完成

以下是部分截图：

<div>
<img width="30%" height="30%" src="https://github.com/Zoctan/SmartHub/blob/app/screenshot/guide3.png"/>

<img width="30%" height="30%" src="https://github.com/Zoctan/SmartHub/blob/app/screenshot/login.png"/>

<img width="30%" height="30%" src="https://github.com/Zoctan/SmartHub/blob/app/screenshot/app.png"/>
</div>

<div>
<img width="30%" height="30%" src="https://github.com/Zoctan/SmartHub/blob/app/screenshot/qr.png"/>

<img width="30%" height="30%" src="https://github.com/Zoctan/SmartHub/blob/app/screenshot/hub_list.png"/>

<img width="30%" height="30%" src="https://github.com/Zoctan/SmartHub/blob/app/screenshot/slit.png"/>
</div>

<div>
<img width="30%" height="30%" src="https://github.com/Zoctan/SmartHub/blob/app/screenshot/nav.png"/>

<img width="30%" height="30%" src="https://github.com/Zoctan/SmartHub/blob/app/screenshot/hub_now.png"/>

<img width="30%" height="30%" src="https://github.com/Zoctan/SmartHub/blob/app/screenshot/hub_spare.png"/>
</div>

<div>
<img width="30%" height="30%" src="https://github.com/Zoctan/SmartHub/blob/app/screenshot/timer_list.png"/>

<img width="30%" height="30%" src="https://github.com/Zoctan/SmartHub/blob/app/screenshot/timer_setting.png"/>

<img width="30%" height="30%" src="https://github.com/Zoctan/SmartHub/blob/app/screenshot/user.png"/>
</div>

<div>
<img width="30%" height="30%" src="https://github.com/Zoctan/SmartHub/blob/app/screenshot/about.png"/>

<img width="30%" height="30%" src="https://github.com/Zoctan/SmartHub/blob/app/screenshot/night.png"/>

<!--<img width="30%" height="30%" src="https://github.com/Zoctan/SmartHub/blob/app/screenshot/"/>-->
</div>

# 命名规范

由于经常出现命名犯难问题，所以写下一些自己总结的规范

## 布局 Layout：

prefix_where

 prefix   |             usage
:-------: | :---------------------------:
activity_ |         activity 内容视图
fragment_ |         fragment 内容视图
  item_   | 在list/recycler/gridview/等使用的项
 layout_  |      在include标签引用的可复用布局
  view_   |           自定义的扩展视图

示例：

- activity_main
- fragment_user_detail
- item_article
- layout_actionbar_backbutton
- view_menu

## 字符串 String：

where_description

复用：all_description

示例：

- article_detail_title
- feedback_explanation
- feedback_namehint
- all_done

## Drawable：

where_description_size

复用：all_description_size

示例：

- article_detail_placeholder
- all_info_icon
- all_info_icon_large
- all_info_icon_32dp

## ID：

what_where_description

开头大写主要是因为控件的命名就是这样

示例：

- TabLayout_main
- ImageView_menu_profile
- TextView_article_detail_title