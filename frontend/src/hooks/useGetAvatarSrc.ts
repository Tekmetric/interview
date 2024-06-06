import avatar1 from "../../assets/avatar-1.png";
import avatar2 from "../../assets/avatar-2.png";
import avatar3 from "../../assets/avatar-3.png";
import avatar4 from "../../assets/avatar-4.png";
import avatar5 from "../../assets/avatar-5.png";

export function useGetAvatarSrc() {
  return (avatarFile: string) => {
    switch (avatarFile) {
      case "avatar-1":
        return avatar1;
      case "avatar-2":
        return avatar2;
      case "avatar-3":
        return avatar3;
      case "avatar-4":
        return avatar4;
      case "avatar-5":
        return avatar5;
      default:
        return avatar1;
    }
  };
}
