import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'home_screen.dart';

class SplashScreen extends StatefulWidget {
  @override
  State<SplashScreen> createState() => SplashScreenState();
}

class SplashScreenState extends State<SplashScreen> {
  startTimer() async {
    // pause for a while then start the home screen
    var duration = Duration(seconds: 3);
    return Timer(duration, () {
      Navigator.push(context, PageRouteBuilder(
          transitionDuration: Duration(seconds: 2), pageBuilder: (_, __, ___) => HomeScreen()));
    });
  }

  @override
  void initState() {
    // set this screen as a full screen
    SystemChrome.setEnabledSystemUIOverlays([]);
    super.initState();
    startTimer();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Hero(
          tag: "splash",
          child: Image.asset(
            'assets/images/safely_logo.png',
            width: 100.0,
            height: 100.0,
          ),
        ),
      ),
    );
  }
}
