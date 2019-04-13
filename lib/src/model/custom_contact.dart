import 'dart:convert';

import 'package:contacts_service/contacts_service.dart';
import 'package:pref_dessert/pref_dessert.dart';

class CustomContact {
  final Contact contact;
  bool isChecked;

  CustomContact({this.contact, this.isChecked = false});
}


class JsonCustomContactDesSer extends DesSer<CustomContact> {
  @override
  CustomContact deserialize(String s) {
    var map = json.decode(s);
    return CustomContact(contact: Contact.fromMap(map));
  }

  @override
  // TODO: implement key
  String get key => null;

  @override
  String serialize(CustomContact t) {
    var map = {"contact":t.contact.displayName};
    return json.encode(map);
  }

}
