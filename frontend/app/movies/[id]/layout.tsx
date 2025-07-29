import { Metadata } from "next";
import { ReactNode } from "react";

export const metadata: Metadata = {
  title: "MovieBase - Discover Trending and Top-Rated Movies",
  description:
    "MovieBase is your go-to platform to explore, discover, and keep track of trending and top-rated movies. Dive into curated lists, detailed movie info, and more!",
};

const Layout = ({ children }: { children: ReactNode }) => {
  return (
    <main className="md:max-w-[960px] lg:max-w-[1280px] mx-auto my-20">
      {children}
    </main>
  );
};

export default Layout;
