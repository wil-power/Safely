import 'package:flutter/material.dart';
import "package:easy_dialogs/easy_dialogs.dart";
import 'package:safely/src/screens/timer_screen.dart';
import 'duration_dialog.dart';
import '../misc/permissions.dart' as perm;

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  double margin = 1.0;
  String selected;
  String durationSelected;
  bool takeOffstage = true;
  bool takeDurationLabelOffstage = true;

  @override
  void initState() {
    super.initState();
    perm.requestPermissions();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _buildHomeScreen(),
    );
  }

  Widget _buildHomeScreen() {
    return ListView(
      children: <Widget>[
        Container(
          padding: const EdgeInsets.only(left: 8.0),
          height: MediaQuery.of(context).size.height,
          width: MediaQuery.of(context).size.width,
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              children: <Widget>[
                // AppBar
                Padding(
                    padding: const EdgeInsets.only(top: 8.0),
                    child: _buildAppBar()),
                SizedBox(
                  height: 70.0,
                ),
                _buildButtonToTriggerDialog(),
                SizedBox(
                  height: 50.0,
                ),
                _buildButtonToTriggerDurationDialog(),
                SizedBox(
                  height: 50.0,
                ),
                _buildTextField(),
                SizedBox(
                  height: 30.0,
                ),
                _buildStartTimerButton()
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildAppBar() {
    return Container(
      width: MediaQuery.of(context).size.width,
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: <Widget>[
          Container(
            decoration: ShapeDecoration(
              shape: CircleBorder(
                  side: BorderSide(
                width: 4.0,
                color: Colors.white70,
              )),
            ),
            height: 50,
            width: 50,
            child: Hero(
                tag: "splash",
                child: Image.asset("assets/images/safely_logo.png")),
          ),

          Spacer(flex: 2,),
          IconButton(
            icon: Icon(
              Icons.settings,
              color: Colors.black54,
              size: 30.0,
            ),
            onPressed: () {
              print("Settings button pressed!");
            },
          ),
        ],
      ),
    );
  }

  Widget _buildButtonToTriggerDialog() {
    return RawMaterialButton(
      onPressed: () {
        _openDialog();
      },
      splashColor: Colors.grey,
      child: Container(
        width: 300.0,
        height: 50.0,
        decoration: ShapeDecoration(shape: OutlineInputBorder()),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.only(left: 16.0),
              child: Text('Choose activity'),
            ),
            Spacer(flex: 1,),
            Padding(
              padding: const EdgeInsets.only(right: 16.0),
              child: Icon(
                Icons.arrow_drop_down,
                size: 18.0,
              ),
            )
          ],
        ),
      ),
    );
  }

  Widget _buildButtonToTriggerDurationDialog() {
    return RawMaterialButton(
      onPressed: () {
        _showDurationPickerDialog();
      },
      splashColor: Colors.grey,
      child: Container(
        width: 300.0,
        height: 50.0,
        decoration: ShapeDecoration(shape: OutlineInputBorder()),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.only(left: 16.0),
              child: Text('Select duration'),
            ),
            Spacer(flex: 1,),
            Padding(
              padding: const EdgeInsets.only(right: 16.0),
              child: Icon(
                Icons.arrow_drop_down,
                size: 18.0,
              ),
            )
          ],
        ),
      ),
    );
  }

  String initial = "Buying a phone from Circle";

  List<String> dialogList = [
    'Taking a walk in the neighborhood',
    "Buying food from Madam Obeng's",
    "Buying a phone from Circle",
    "Going to Kobby's house",
    "Going out for a run through the neighborhood"
  ];

  List<double> durationList = [1];

  _openDialog() {
    showDialog(
        context: context,
        builder: (context) => SingleChoiceConfirmationDialog<String>(
              title: Text("Choose activity"),
              initialValue: initial,
              items: dialogList,
              onSelected: _onSelected,
            ));
  }

  _onSelected(dynamic value) {
    print('Selected $value');
  }

  void _showDurationPickerDialog() async {
    // this will contain the result of the Navigator.pop(context, result)
    final selectedMins = await showDialog(
        context: context, builder: (context) => DurationDialog());
  }

  Widget _buildTextField() {
    return Container(
      height: 220,
      margin: const EdgeInsets.all(8.0),
      padding: EdgeInsets.only(bottom: 40.0),
      child: TextField(
        keyboardType: TextInputType.multiline,
        maxLines: 99,
        decoration: InputDecoration(
            hintText: "What are you wearing? Are you picking a taxi?",
            border: OutlineInputBorder()),
      ),
    );
  }

  Widget _buildStartTimerButton() {
    return RawMaterialButton(
        onPressed: () {
          Navigator.push(
              context, MaterialPageRoute(builder: (context) => TimerScreen()));
        },
        splashColor: Colors.green,
        padding: const EdgeInsets.symmetric(horizontal: 8.0),
        fillColor: Colors.grey,
        child: Text("Start Timer"),
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.all(Radius.circular(10.0))));
  }
}
