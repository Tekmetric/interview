import React from 'react';
import { Modal, TouchableOpacity, Image, StyleSheet, Dimensions } from 'react-native';

type ImageModalProps = {
  visible: boolean;
  onClose: () => void;
  uri: string;
};

export default function ImageModal({ visible, onClose, uri }: ImageModalProps) {
  return (
    <Modal visible={visible} transparent={true} animationType="fade" onRequestClose={onClose}>
      <TouchableOpacity style={styles.modalOverlay} activeOpacity={1} onPress={onClose}>
        <Image source={{ uri }} style={styles.modalImage} resizeMode="contain" />
      </TouchableOpacity>
    </Modal>
  );
}

const styles = StyleSheet.create({
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.9)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalImage: {
    width: Dimensions.get('window').width * 0.9,
    height: Dimensions.get('window').height * 0.7,
  },
});
