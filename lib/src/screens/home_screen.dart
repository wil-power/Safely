import 'package:flutter/material.dart';

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  List<DropdownMenuItem<String>> dropDownItems = [
    DropdownMenuItem(
      child: Text("Taking a walk"),
      value: "Taking a walk",
    ),
    DropdownMenuItem(
      child: Text("Going for a run"),
      value: "Going for a run",
    ),
    DropdownMenuItem(
      child: Text("Getting a haircut"),
      value: "Getting a haircut",
    )
  ];
  List<DropdownMenuItem<String>> durationDropDown = [
    DropdownMenuItem(
      child: Text("5"),
      value: "5",
    ),
    DropdownMenuItem(child: Text("10"), value: "10"),
    DropdownMenuItem(
      child: Text("15"),
      value: "15",
    ),
    DropdownMenuItem(child: Text("20"), value: "20"),
    DropdownMenuItem(
      child: Text("25"),
      value: "25",
    ),
    DropdownMenuItem(child: Text("30"), value: "30"),
    DropdownMenuItem(child: Text("35"), value: "35",),
    DropdownMenuItem(child: Text("40"), value: "40"),
    DropdownMenuItem(child: Text("45"), value: "45",),
    DropdownMenuItem(child: Text("50"), value: "50"),
    DropdownMenuItem(child: Text("55"), value: "55",),
    DropdownMenuItem(child: Text("60"), value: "60",)

  ];
  String selected;
  String durationSelected;
  bool takeOffstage = true;
  bool takeDurationLabelOffstage = true;

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Container(
        padding: const EdgeInsets.only(top: 8.0, left: 8.0),
        height: double.infinity,
        width: MediaQuery.of(context).size.width,
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            children: <Widget>[
              // AppBar
              Padding(
                  padding: const EdgeInsets.only(top: 16.0),
                  child: _buildAppBar()),
              SizedBox(
                height: 100.0,
              ),
              Center(
                child: Column(
                  children: <Widget>[
                    Offstage(
                      child: Text("Choose an activity"),
                      offstage: takeOffstage,
                    ),
                    Container(
                      width: 310.0,
                      decoration: ShapeDecoration(shape: OutlineInputBorder()),
                      child: Padding(
                        padding: const EdgeInsets.only(left: 8.0),
                        child: DropdownButtonHideUnderline(
                          child: DropdownButton(
                              items: dropDownItems,
                              value: selected,
                              hint: Text("What do you plan on doing?"),
                              onChanged: (value) {
                                setState(() {
                                  selected = value;
                                  takeOffstage = false;
                                });
                              }),
                        ),
                      ),
                    ),
//                    SizedBox(height: 50.0,),
//                    Offstage(
//                      child: Text("Select duration"),
//                      offstage: takeOffstage,
//                    ),
//                    Container(
//                      width: 310.0,
//                      decoration: ShapeDecoration(shape: OutlineInputBorder()),
//                      child: Padding(
//                        padding: const EdgeInsets.only(left: 8.0),
//                        child: DropdownButtonHideUnderline(
//                          child: ButtonTheme(
//                            alignedDropdown: true,
//                            child: DropdownButton(
//                                items: durationDropDown,
//                                value: durationSelected,
//                                hint: Text("How long will you take?"),
//                                elevation: 12,
//                                onChanged: (val) {
//                                  setState(() {
//                                    durationSelected = val;
//                                    takeDurationLabelOffstage = false;
//                                  });
//                                }),
//                          ),
//                        ),
//                      ),
//                    ),
//                    SizedBox(height: 18.0,),
                    //_buildDropDownButton()
                  ],
                ),
              )
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildDropDownButton() {
    String dropdown1Value;
    return Padding(
      padding: const EdgeInsets.all(24.0),
      child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          children: <Widget>[
      ListTile(
      title: const Text('Simple dropdown:'),
      trailing: DropdownButton<String>(
        value: dropdown1Value,
        onChanged: (String newValue) {
          setState(() {
            dropdown1Value = newValue;
          });
        },
        items: <String>['One', 'Two', 'Free', 'Four', 'Five', 'Six', 'Seven'].map<DropdownMenuItem<String>>((String value) {
          return DropdownMenuItem<String>(
            value: value,
            child: Text(value),
          );
        }).toList(),
      ),
    ), ]));
  }
  Widget _buildAppBar() {
    return Row(
      children: <Widget>[
        Container(
          decoration: ShapeDecoration(
            shape: CircleBorder(
                side: BorderSide(
              width: 4.0,
              color: Colors.amber,
            )),
          ),
          child: Text("LOGO"),
        ),
        SizedBox(
          width: 300.0,
        ),
        Icon(
          Icons.settings,
          color: Colors.amberAccent[100],
          size: 32.0,
        ),
      ],
    );
  }
}
