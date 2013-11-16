DynamicShareActionProvider
==========================

My own implementation of the [ShareActionProvider](http://developer.android.com/reference/android/support/v7/widget/ShareActionProvider.html) by Google.

The differences:
----------------

* Sharing is done **dynamically** now!

  This means that your app specifies the type of the shared data first and generates the data when an app is chosen for sharing. No more need to define what to share when the Activity is created. The content can now be set dynamically. :smile:

  There are two types of listeners so that you can also generate the data in an AsyncTask.

* There is **no [app icon of the most often used app](http://developer.android.com/images/ui/actionbar-shareaction.png)** appearing next to the share icon.

  Other icons often do not match the app theme.
 
* The shown app list is **not limited to three apps** and a "See all" entry first.

  Why should the user not see all apps for that action?
  
* You can adjust/must **set the icon manually**.

  Don't worry. :wink:

Big thanks to:
--------------

* [SimplicityApks](https://github.com/SimplicityApks) for some ideas