import 'package:flutter/material.dart';
import "package:easy_dialogs/easy_dialogs.dart";
import 'package:safely/src/screens/timer_screen.dart';
import 'duration_dialog.dart';
import 'package:safely/src/model/activity_information.dart';
import 'package:safely/src/screens/chosen_contacts_screen.dart';

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen>
    with SingleTickerProviderStateMixin {
  double margin = 1.0;
  String selected;
  String durationSelected = "Select duration";
  String userActivity = "Choose activity";

//  String chooseActivityLabel = displayActivityLabel ? "Choose Activity" : "";
//  static bool displayActivityLabel = false;
//  String chooseDurationLabel = displayDurationLabel ? "Choose Duration" : "";
//  static bool displayDurationLabel = false;

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
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
          height: MediaQuery
              .of(context)
              .size
              .height,
          width: MediaQuery
              .of(context)
              .size
              .width,
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
      width: MediaQuery
          .of(context)
          .size
          .width,
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
          Spacer(
            flex: 2,
          ),
          IconButton(
            icon: Icon(
              Icons.settings,
              color: Colors.black54,
              size: 30.0,
            ),
            onPressed: () {
              _showBottomSheet(context);
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
              child: Text(userActivity),
            ),
            Spacer(
              flex: 1,
            ),
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
              child: Text(durationSelected),
            ),
            Spacer(
              flex: 1,
            ),
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

  String initial = "Taking a walk alone in the neighborhood";
  List<String> dialogList = [
    'Taking a walk alone in the neighborhood',
    'Picking a taxi',
    "Buying a phone from Circle",
    "Going to Kobby's house",
    "Going out for a run in the neighborhood",
  ];

  _openDialog() {
    showDialog(
        context: context,
        builder: (context) =>
            SingleChoiceConfirmationDialog<String>(
              title: Text("Choose Activity"),
              initialValue: initial,
              items: dialogList,
              onSubmitted: (string) {
                setState(() {
                  userActivity = string;
                });
              },
            ));
  }

  void _showDurationPickerDialog() async {
    // this will contain the result of the Navigator.pop(context, result)
    final results = await showDialog(
        context: context, builder: (context) => DurationDialog());
    String hrText = results[0] == 1 || results[0] == 0 ? "hr" : "hrs";
    String minText = results[1] == 1 || results[1] == 0 ? "min" : "mins";

    setState(() {
      if (results != null) {
        hours = results[0];
        minutes = results[1];
        durationSelected = "${results[0]}$hrText, ${results[1]}$minText";
      }
    });
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

  int hours;
  int minutes;

  Widget _buildStartTimerButton() {
    return RawMaterialButton(
        onPressed: () {
          if (userActivity.toLowerCase() != "choose activity" &&
              (hours != null && minutes != null)) {
            var infoObj = UserActivityInfo(
                activityTitle: userActivity,
                duration: Duration(hours: hours, minutes: minutes));

            Navigator.push(
                context,
                MaterialPageRoute(
                    builder: (context) =>
                        TimerScreen(
                          userActivityInfo: infoObj,
                        )));
          } else if (userActivity.toLowerCase() == "choose activity") {
            print("Pick an activity");
          } else if (hours == 0 && minutes == 0) {
            print("Pick a duration");
          }
        },
        splashColor: Colors.green,
        padding: const EdgeInsets.symmetric(horizontal: 8.0),
        fillColor: Colors.grey,
        child: Text("Start Timer"),
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.all(Radius.circular(10.0))));
  }

  void _showBottomSheet(BuildContext context) {
    showModalBottomSheet(
        context: context,
        builder: (context) {
          return Container(
            decoration: BoxDecoration(
              borderRadius: BorderRadius.only(
                  topLeft: Radius.circular(10.0),
                  topRight: Radius.circular(10.0)),),
            child: _buildBottomSheetContent(),
          );
        });
  }

  Widget _buildBottomSheetContent() {
    return Column(
      children: <Widget>[
        Center(child: Text("Settings",
          style: TextStyle(color: Colors.black, fontWeight: FontWeight.bold),),),
        ListTile(
          leading: Icon(Icons.contacts, color: Colors.blueGrey,),
          title: Text("View selected contacts"),
          trailing: Icon(Icons.keyboard_arrow_right, size: 18.0, color: Colors.blueGrey,),
          onTap: () {
            Navigator.push(context, MaterialPageRoute(builder: (BuildContext context) => ChosenContactsPage()));
          },
        )
      ],
    );
  }
}
