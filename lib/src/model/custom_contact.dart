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
    print("Deserialized: $s");
    var con = Contact(givenName: s);
    con.displayName = s;
    return CustomContact(contact: con);
  }

  @override
  // TODO: implement key
  String get key => null;

  @override
  String serialize(CustomContact t) {
    print("Serialize: ${t.contact.displayName}");
    return "${t.contact.displayName}";
  }

}
