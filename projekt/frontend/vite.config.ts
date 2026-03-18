/// <reference types="vitest/config" />
import {defineConfig, type UserConfig} from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
  server: {
    host: '0.0.0.0',
    port: 8080,
    watch : {
      usePolling: true
    },
    proxy: {
        '/api': {
            target: 'http://backend:8080',
            changeOrigin: true
        }
    }
  },
  plugins: [react(), tailwindcss()],
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
