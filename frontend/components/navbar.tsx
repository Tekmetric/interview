import Link from "next/link";
import { Logo } from "./ui/logo";

export const Navbar = () => {
  return (
    <nav className="h-14 bg-navbar-background">
      <div className="max-w-[1280px] mx-auto flex items-center h-full">
        <Link href="/">
          <Logo />
        </Link>
      </div>
    </nav>
  );
};
