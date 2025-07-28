"use client";

import { cn } from "@/utils/cn";
import {
  useRef,
  createContext,
  DetailedHTMLProps,
  HTMLAttributes,
  useContext,
  ButtonHTMLAttributes,
  RefObject,
} from "react";
import { ChevronLeft, ChevronRight } from "./icons";

type CarouselContextProps = {
  onScrollLeft: () => void;
  onScrollRight: () => void;
  contentRef: RefObject<HTMLDivElement>;
};
const CarouselContext = createContext<CarouselContextProps>(
  {} as CarouselContextProps
);

const Carousel = ({
  className,
  ...rest
}: DetailedHTMLProps<HTMLAttributes<HTMLDivElement>, HTMLDivElement>) => {
  const contentRef = useRef<HTMLDivElement>(null);

  const onScrollLeft = () => {
    const element = contentRef.current;
    if (!element) {
      return;
    }

    const visibleWidth = element.getBoundingClientRect().width;
    element?.scrollTo({
      left: Math.max(0, element.scrollLeft - visibleWidth * 0.9),
      behavior: "smooth",
    });
  };

  const onScrollRight = () => {
    const container = contentRef.current;
    if (!container) {
      return;
    }

    const visibleWidth = container.getBoundingClientRect().width;
    container?.scrollTo({
      left: container.scrollLeft + visibleWidth * 0.9,
      behavior: "smooth",
    });
  };

  return (
    <CarouselContext.Provider
      value={{
        onScrollLeft,
        onScrollRight,
        contentRef: contentRef as RefObject<HTMLDivElement>,
      }}
    >
      <div className={cn("relative isolate group", className)} {...rest} />
    </CarouselContext.Provider>
  );
};

const buttonBaseStyles = [
  "z-10 absolute top-1/2 -translate-y-1/2",
  "hidden lg:flex items-center justify-center",
  "opacity-0 group-hover:opacity-100 focus-visible:opacity-100",
  "bg-black/40",
  "w-10 h-20",
  "cursor-pointer",
  "hover:text-accent focus-visible:text-accent",
  "border-1 border-white/70 rounded-lg outline-0",
];

const CarouselScrollLeftButton = ({
  className,
  onClick,
  ...rest
}: DetailedHTMLProps<
  ButtonHTMLAttributes<HTMLButtonElement>,
  HTMLButtonElement
>) => {
  const { onScrollLeft } = useContext(CarouselContext);

  return (
    <button
      className={cn(...buttonBaseStyles, "left-0 -translate-x-1/4", className)}
      onClick={(e) => {
        onClick?.(e);
        onScrollLeft();
      }}
      {...rest}
    >
      <ChevronLeft />
    </button>
  );
};

const CarouselScrollRightButton = ({
  className,
  onClick,
  ...rest
}: DetailedHTMLProps<
  ButtonHTMLAttributes<HTMLButtonElement>,
  HTMLButtonElement
>) => {
  const { onScrollRight } = useContext(CarouselContext);

  return (
    <button
      className={cn(...buttonBaseStyles, "right-0 translate-x-1/4", className)}
      onClick={(e) => {
        onClick?.(e);
        onScrollRight();
      }}
      {...rest}
    >
      <ChevronRight />
    </button>
  );
};

const CarouselContent = ({
  className,
  ...rest
}: DetailedHTMLProps<HTMLAttributes<HTMLDivElement>, HTMLDivElement>) => {
  const { contentRef } = useContext(CarouselContext);

  return (
    <div
      ref={contentRef}
      className={cn("flex space-x-4 overflow-auto no-scrollbar", className)}
      {...rest}
    />
  );
};

Carousel.Content = CarouselContent;
Carousel.ScrollLeftButton = CarouselScrollLeftButton;
Carousel.ScrollRightButton = CarouselScrollRightButton;

export { Carousel };
