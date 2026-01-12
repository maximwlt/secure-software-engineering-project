/// <reference types="vitest/config" />
import {defineConfig, type UserConfig} from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': '/src'
    }
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/tests/setup.ts',
    css: true,
    deps: {
      inline: ['@exodus/bytes']
    }
  }
} as UserConfig)
