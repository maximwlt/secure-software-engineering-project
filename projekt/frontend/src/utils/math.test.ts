import { describe, it, expect } from 'vitest'
import { add } from './math'

describe('add', () => {
    it('addiert zwei Zahlen korrekt', () => {
        expect(add(2, 3)).toBe(5)
    })

    it('funktioniert auch mit negativen Zahlen', () => {
        expect(add(-1, -2)).toBe(-3)
    })
})
