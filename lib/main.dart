import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'src/screens/home_screen.dart';
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
        body: HomeScreen(),
      )
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);
  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const channel = EventChannel('foo');

  @override
  Widget build(BuildContext context) {
    return Text("This shit works", style: TextStyle(color: Colors.blue),);
  }
}
