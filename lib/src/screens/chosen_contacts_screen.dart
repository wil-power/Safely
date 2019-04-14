import 'dart:async';

import 'package:flutter/material.dart';
import 'package:pref_dessert/pref_dessert.dart';
import 'package:safely/src/model/custom_contact.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ChosenContactsPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    // TODO: implement createState
    return ChosenContactsPageState();
  }
}

class ChosenContactsPageState extends State<ChosenContactsPage> {
  List<CustomContact> savedContacts = List();

  retrieveSavedContacts() async {
    var prefs = await SharedPreferences.getInstance();
    var repo = PreferencesRepository(prefs, JsonCustomContactDesSer());
    var temp = repo.findAll();
    temp.forEach((tem) {
      setState(() {
        savedContacts.add(tem);
      });
    });
  }

  @override
  void dispose() {
    savePreferences();
    super.dispose();
  }

  savePreferences() async{
    var prefs = await SharedPreferences.getInstance();
    var repo =
    PreferencesRepository<CustomContact>(prefs, JsonCustomContactDesSer());
    repo.saveAll(savedContacts);
  }

  @override
  void initState() {
    super.initState();
    retrieveSavedContacts();
  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return SafeArea(
      child: Scaffold(
        body: savedContacts.isEmpty
            ? Center(
                child: CircularProgressIndicator(),
              )
            : _buildChosenContactsList(),
      ),
    );
  }

  Widget _buildChosenContactsList() {
    return ListView.builder(
      itemCount: savedContacts.length,
      itemBuilder: (context, index) {
        CustomContact contact = savedContacts[index];
//        var phonesList = contact.contact.phones.toList();

        return _buildListTile(contact, index);
      },
    );
  }

  Widget _buildListTile(CustomContact customContact, int index) {
    return Dismissible(
      background: Container(color: Colors.red,),
      onDismissed: (direction) {
        setState(() {
          savedContacts.removeAt(index);
        });
      },
      child: ListTile(
        leading: CircleAvatar(
          child: Text(
            (customContact.contact.displayName[0].toUpperCase()),
            style: TextStyle(color: Colors.white),
          ),
        ),
        title: Text(customContact.contact.displayName ?? ""),
//      subtitle: list.length >= 1 && list[0]?.value != null
//          ? Text(list[0].value)
//          : Text(''),
      ), key: Key(customContact.contact.displayName),
    );
  }

}
