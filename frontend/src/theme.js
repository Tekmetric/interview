import { extendTheme } from "@chakra-ui/react";

const customTheme = extendTheme({
  initialColorMode: 'dark',
  colors: {
    brand: {
      100: "#f15725",
    },
  },
  components: {
    Spinner: {
      baseStyle: {
        color: "brand.100",
        size: "xl",
      },
    },
    Text: {
      baseStyle: {
        color: "grey",
        fontSize: "18px",
      },
    },
    Alert: {
      baseStyle: {
        bg: "yellow",
        color: "black",
      },
    },
    Select: {
      baseStyle: {
        bg: "green",
        color: "white",
        borderColor: "teal",
      },
    },
  },
});

export default customTheme
