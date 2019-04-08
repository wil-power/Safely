
import 'package:flutter/material.dart';

class DurationDialog extends StatefulWidget {

  @override
  createState() => DurationDialogState();
}

class DurationDialogState extends State<DurationDialog> {
  double minutes = 0;
  double hours = 0;


  @override
  Widget build(BuildContext context) {

    return AlertDialog(
      title: Text('Select Duration'),
      content: Container(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: <Widget>[
            Slider(
              value: hours,
              label: hours.round().toString(),
              min: 0,
              max: 12,
              divisions: 12,
              onChanged: (value) {
                setState(() {
                  hours = value;
                });
              },
            ),
            Padding(padding: EdgeInsets.only(top: 32.0),),
            Slider(
              min: 0,
              max: 60,
              value: minutes,
              label: minutes.round().toString(),
              divisions: 60,
              onChanged: (val) {
                setState(() {
                  minutes = val;
                });
              },
            )
          ],
        ),
      ),
      actions: <Widget>[
        FlatButton(
          onPressed: (){
            Navigator.pop(context, minutes);
          },
          child: Text("Done"),
        )
      ],
    );
  }
}