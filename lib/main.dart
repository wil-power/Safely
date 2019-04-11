import 'package:flutter/material.dart';
import 'src/screens/home_screen.dart';
import 'src/screens/splash_screen.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: Scaffold(
//        resizeToAvoidBottomPadding: false,
        body: SplashScreen(),
      ),
      routes: <String, WidgetBuilder> {
        '/home': (context) => HomeScreen()
      },
    );
  }
}