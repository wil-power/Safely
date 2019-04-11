import 'package:permission_handler/permission_handler.dart';

requestPermissions() async {
  Map<PermissionGroup, PermissionStatus> permissions = await PermissionHandler()
      .requestPermissions([
    PermissionGroup.contacts,
    PermissionGroup.sms,
    PermissionGroup.location
  ]);

}
