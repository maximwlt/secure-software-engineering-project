import DOMPurify from "dompurify";
import { marked } from "marked";
import { useState, useEffect } from "react";

export function SafeMarkdown({ markdown }: { markdown: string }) {
    const [cleanHtml, setCleanHtml] = useState<string>("");

    useEffect(() => {
        const parseMarkdown = async () => {
            try {
                const html = await marked(markdown);
                const clean = DOMPurify.sanitize(html);
                setCleanHtml(clean);
            } catch (err) {
                console.error("Markdown Parsing Error:", err);
                // Fallback: roher Text in einem <pre>-Tag
                setCleanHtml(DOMPurify.sanitize(`<pre>${markdown}</pre>`));
            }
        };
        parseMarkdown();
    }, [markdown]);
    return <div dangerouslySetInnerHTML={{ __html: cleanHtml }} />;
}