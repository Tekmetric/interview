import React, { ComponentProps } from "react";
import { cn } from "@/utils/cn";
import Image from "next/image";
import { getPosterImageUrl, type PosterSizes } from "@/api/api";

const PosterCard = ({
  className,
  ...rest
}: React.HTMLAttributes<HTMLDivElement>) => {
  return (
    <div
      className={cn(
        "rounded-lg rounded-tl-none",
        "flex flex-col gap-2",
        "bg-poster-card-background",
        "overflow-hidden",
        className
      )}
      {...rest}
    />
  );
};

const PosterCardContent = ({
  className,
  ...rest
}: React.HTMLAttributes<HTMLDivElement>) => {
  return (
    <div className={cn("p-4", "flex flex-col gap-2", className)} {...rest} />
  );
};

const PosterCardImage = ({
  className,
  src,
  size,
  ...rest
}: { src: string; size: PosterSizes } & ComponentProps<typeof Image>) => {
  return (
    // Width and height represent intrinsic image size and only used for the aspect ratio to prevent the layout shift
    // eslint-disable-next-line jsx-a11y/alt-text
    <Image
      src={getPosterImageUrl(src, size)}
      width={185}
      height={278}
      className={cn("select-none", className)}
      {...rest}
    />
  );
};

const PosterCardRating = ({ children }: { children: React.ReactNode }) => {
  return (
    <div className="flex items-center gap-1 text-md text-white">
      <StarIcon className="w-4 h-4 text-accent" />
      <span className="leading-none">{children}</span>
    </div>
  );
};

const PosterCardTitle = ({
  title,
  children,
}: {
  title: string;
  children: React.ReactNode;
}) => {
  return (
    <span title={title} className="text-white text-md line-clamp-2">
      {children}
    </span>
  );
};

const PosterCardDescription = ({ children }: { children: React.ReactNode }) => {
  return <span className="text-white text-sm">{children}</span>;
};

const StarIcon = (props: React.SVGProps<SVGSVGElement>) => {
  return (
    <svg
      width="24"
      height="24"
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      fill="currentColor"
      role="presentation"
      {...props}
    >
      <path d="M12 20.1l5.82 3.682c1.066.675 2.37-.322 2.09-1.584l-1.543-6.926 5.146-4.667c.94-.85.435-2.465-.799-2.567l-6.773-.602L13.29.89a1.38 1.38 0 0 0-2.581 0l-2.65 6.53-6.774.602C.052 8.126-.453 9.74.486 10.59l5.147 4.666-1.542 6.926c-.28 1.262 1.023 2.26 2.09 1.585L12 20.099z"></path>
    </svg>
  );
};

PosterCard.Image = PosterCardImage;
PosterCard.Content = PosterCardContent;
PosterCard.Rating = PosterCardRating;
PosterCard.Title = PosterCardTitle;
PosterCard.Description = PosterCardDescription;

export { PosterCard };
