import React, { useRef } from 'react';
import { Image, View, Text, StyleSheet, TouchableOpacity, Animated } from 'react-native';
import { Feather } from '@expo/vector-icons';
import { spacing, typography } from '@/config/theme';
import { useTheme } from '@/context/themeContext';
import { useSavedItems } from '@/context/savedItemsContext';
import { ArtCrime } from '@/types/artCrime';
import ImageModal from '../modals/imageModal';

type GalleryCardProps = {
  item: ArtCrime;
  onPress: () => void;
};

export default function GalleryCard({ item, onPress }: GalleryCardProps) {
  const { theme } = useTheme();
  const { saveItem, removeItem, isItemSaved } = useSavedItems();
  const isSaved = isItemSaved(item.uid);
  const [isModalVisible, setIsModalVisible] = React.useState(false);
  const scaleAnim = useRef(new Animated.Value(1)).current;

  const handleLongPress = () => {
    Animated.sequence([
      Animated.timing(scaleAnim, {
        toValue: 1.1,
        duration: 200,
        useNativeDriver: true,
      }),
      Animated.timing(scaleAnim, {
        toValue: 1,
        duration: 200,
        useNativeDriver: true,
      }),
    ]).start(() => {
      setIsModalVisible(true);
    });
  };

  const handleSavePress = () => {
    if (isSaved) {
      removeItem(item.uid);
    } else {
      saveItem(item);
    }
  };

  return (
    <>
      <TouchableOpacity
        onPress={onPress}
        onLongPress={handleLongPress}
        style={[styles.card, { backgroundColor: theme.colors.card }]}
      >
        <Animated.View style={[styles.imageContainer, { transform: [{ scale: scaleAnim }] }]}>
          <Image
            source={{ uri: item.images?.[0]?.thumb ?? '' }}
            style={styles.image}
            resizeMode="cover"
          />
          <TouchableOpacity
            style={[styles.saveButton, { backgroundColor: theme.colors.background }]}
            onPress={handleSavePress}
          >
            <Feather
              name={isSaved ? 'bookmark' : 'bookmark'}
              size={20}
              color={isSaved ? theme.colors.primary : theme.colors.text}
            />
          </TouchableOpacity>
        </Animated.View>
        <Text numberOfLines={2} style={[styles.title, { color: theme.colors.text }]}>
          {item.title}
        </Text>
        <MetadataRow icon="user" value={item.maker || 'Unknown'} color={theme.colors.text} />
        <MetadataRow icon="layers" value={item.materials || 'Unknown'} color={theme.colors.text} />
      </TouchableOpacity>

      <ImageModal
        visible={isModalVisible}
        onClose={() => setIsModalVisible(false)}
        uri={item.images?.[0]?.original ?? ''}
      />
    </>
  );
}

function MetadataRow({
  icon,
  value,
  color,
}: {
  icon: keyof typeof Feather.glyphMap;
  value: string;
  color: string;
}) {
  return (
    <View style={styles.metadataRow}>
      <Feather name={icon} size={16} color={color} />
      <Text numberOfLines={1} style={[styles.metadataText, { color }]}>
        {value}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    margin: spacing.sm,
    borderRadius: 12,
    backgroundColor: '#fff',
    overflow: 'hidden',
    flex: 1,
    paddingBottom: spacing.sm,
  },
  imageContainer: {
    position: 'relative',
  },
  image: {
    height: 150,
    backgroundColor: '#eee',
  },
  saveButton: {
    position: 'absolute',
    top: spacing.sm,
    right: spacing.sm,
    padding: spacing.xs,
    borderRadius: 20,
  },
  title: {
    paddingHorizontal: spacing.sm,
    paddingTop: spacing.sm,
    fontSize: typography.fontSize.xs,
    fontWeight: 'bold',
    height: 40,
  },
  metadataRow: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: spacing.sm,
    marginBottom: spacing.xs,
  },
  metadataText: {
    paddingLeft: spacing.sm,
    fontSize: typography.fontSize.xs,
    color: '#666',
    flex: 1,
  },
});
