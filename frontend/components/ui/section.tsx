import { DetailedHTMLProps, HTMLAttributes } from "react";
import { cn } from "@/utils/cn";
import { ChevronRight } from "./icons/chevron-right";

const Section = ({
  className,
  ...rest
}: DetailedHTMLProps<HTMLAttributes<HTMLElement>, HTMLElement>) => {
  return (
    <section
      className={cn(
        "max-w-[1280px] mx-auto",
        "flex flex-col gap-4 items-start",
        "overflow-hidden",
        "p-4",
        className
      )}
      {...rest}
    />
  );
};

const SectionHeader = ({
  className,
  ...rest
}: DetailedHTMLProps<
  HTMLAttributes<HTMLHeadingElement>,
  HTMLHeadingElement
>) => {
  return (
    <h2 className={cn("text-accent text-3xl font-bold", className)} {...rest} />
  );
};

const SectionSubheader = ({
  className,
  children,
  ...rest
}: DetailedHTMLProps<HTMLAttributes<HTMLDivElement>, HTMLDivElement>) => {
  return (
    <div
      className={cn(
        "group flex gap-2 items-center leading-none h-6",
        "text-lg leading-0 h-5",
        className
      )}
      {...rest}
    >
      <hr className="border-accent border-0 border-l-4 h-full rounded-full" />
      {children}
    </div>
  );
};
const SectionSubheaderIcon = ({
  className,
  ...rest
}: React.SVGProps<SVGSVGElement>) => {
  return (
    <ChevronRight
      className={cn("h-4 group-hover:text-accent", className)}
      {...rest}
    />
  );
};

Section.Header = SectionHeader;
Section.Subheader = SectionSubheader;
Section.SubheaderIcon = SectionSubheaderIcon;

export { Section };
