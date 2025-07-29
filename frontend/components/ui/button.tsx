import { cn } from "@/utils/cn";
import { ButtonHTMLAttributes, DetailedHTMLProps } from "react";
export const Button = ({
  className,
  ...rest
}: DetailedHTMLProps<
  ButtonHTMLAttributes<HTMLButtonElement>,
  HTMLButtonElement
>) => {
  return (
    <button
      className={cn(
        "rounded-full border-2 border-white/70 px-8 cursor-pointer hover:bg-white/10 h-10",
        className
      )}
      {...rest}
    />
  );
};
