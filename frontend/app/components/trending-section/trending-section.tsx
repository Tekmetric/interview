import Link from "next/link";

import { Section } from "@/components/ui/section";
import { TrendingCarousel } from "./trending-carousel";

export const TrendingSection = () => {
  return (
    <Section className="my-20 h-[510px]">
      <Section.Header>Trending Movies</Section.Header>
      <Link href="/trending">
        <Section.Subheader>
          View More
          <Section.SubheaderIcon />
        </Section.Subheader>
      </Link>
      <TrendingCarousel />
    </Section>
  );
};
