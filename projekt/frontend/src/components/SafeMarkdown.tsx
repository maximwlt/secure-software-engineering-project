import DOMPurify from "dompurify";
import { marked, type Tokens } from "marked";
import { useState, useEffect, useRef } from "react";
import "../styling/SafeMarkdown.css";

export function SafeMarkdown({ markdown }: { markdown: string }) {
    const [cleanHtml, setCleanHtml] = useState<string>("");
    const containerRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const parseMarkdown = async () => {
            try {
                const renderer = new marked.Renderer();

                renderer.image = ({ href, title, text }: Tokens.Image): string => {
                    if (!href) return '';

                    if (href.startsWith('embed:')) {
                        const url = href.replace(/^embed:/, '').trim();
                        const youtubeId = getYoutubeIdFromUrl(url);

                        if (!youtubeId) return '<p>Ungültiger Video-Link</p>';

                        // Platzhalter statt iframe - kein Tracking bis Klick
                        return `
                            <div class="youtube-consent-placeholder" 
                                 data-video-id="${DOMPurify.sanitize(youtubeId)}" 
                                 data-title="${DOMPurify.sanitize(text || 'YouTube Video')}">
                                <div class="consent-overlay"> 
                                    <strong>Empfohlener externer Inhalt</strong>
                                    <p class="consent-notice">
                                        An dieser Stelle finden Sie einen externen Inhalt von YouTube, <br>
                                        der den Artikel ergänzt und von der Redaktion empfohlen wird. <br>
                                        Sie können ihn sich mit einem Klick anzeigen lassen. <br>

                                    </p>
                                    <button class="consent-load-btn" type="button">
                                        Externen Inhalt laden
                                    </button>
                                    <p class="consent-notice">Ich bin damit einverstanden, dass mir externe Inhalt angezeigt werden. <br>
                                       Damit können personenbezogene Daten an Drittanbieter übermittelt werden. <br>
                                       Mehr dazu in den 
                                       <a href="https://policies.google.com/privacy" target="_blank" rel="noopener noreferrer">
                                            Datenschutzrichtlinien
                                       </a>.
                                     </p>
                                </div>
                            </div>`;
                    }

                    const safeHref = DOMPurify.sanitize(href);
                    const safeTitle = title ? DOMPurify.sanitize(title) : '';
                    const safeAlt = text ? DOMPurify.sanitize(text) : '';
                    return `<img src="${safeHref}" title="${safeTitle}" alt="${safeAlt}"/>`;
                };

                // console.log("Parsing Markdown:", JSON.stringify(markdown));
                const html = await marked(markdown, {
                    renderer,
                    gfm: true,
                    breaks: true,
                });

                const clean = DOMPurify.sanitize(html, {
                    ALLOWED_TAGS: [
                        'b', 'em', 'i', 'strong', 'u', 'p',
                        'h1', 'h2', 'h3', 'h4', 'h5', 'h6',
                        'code', 'pre', 'br', 'hr',
                        'table', 'thead', 'tbody', 'tfoot', 'tr', 'td', 'th',
                        'ul', 'ol', 'li',
                        'a', 'img', 'div', 'button', 'span'
                    ],
                    ALLOWED_ATTR: [
                        'href', 'src', 'alt', 'title', 'target', 'rel',
                        'class', 'data-video-id', 'data-title', 'type'
                    ],
                    ALLOWED_URI_REGEXP: /^https?:/i
                });

                setCleanHtml(clean);
            } catch (err) {
                console.error("Markdown Parsing Error:", err);
                setCleanHtml(DOMPurify.sanitize(`<pre>${markdown}</pre>`));
            }
        };
        parseMarkdown();
    }, [markdown]);

    // Event-Delegation für Consent-Buttons
    useEffect(() => {
        const container = containerRef.current;
        if (!container) return;

        const handleConsentClick = (e: MouseEvent) => {
            const target = e.target as HTMLElement;

            if (!target.classList.contains('consent-load-btn')) return;

            const placeholder = target.closest('.youtube-consent-placeholder');
            if (!placeholder) return;

            const videoId = placeholder.getAttribute('data-video-id');
            const title = placeholder.getAttribute('data-title') || 'YouTube Video';

            // Validiere Video-ID nochmals
            if (!videoId || !/^[a-zA-Z0-9_-]{11}$/.test(videoId)) {
                console.error('Ungültige Video-ID');
                return;
            }

            // Erstelle iframe erst nach Zustimmung
            const iframe = document.createElement('iframe');
            iframe.width = '560';
            iframe.height = '315';
            iframe.src = `https://www.youtube-nocookie.com/embed/${videoId}`;
            iframe.title = title;
            iframe.allow = 'accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share';
            iframe.referrerPolicy = 'strict-origin-when-cross-origin';
            iframe.allowFullscreen = true;
            iframe.loading = 'lazy';

            // Ersetze Platzhalter mit iframe
            placeholder.innerHTML = '';
            placeholder.classList.remove('youtube-consent-placeholder');
            placeholder.classList.add('youtube-embed-active');
            placeholder.appendChild(iframe);
        };

        container.addEventListener('click', handleConsentClick);

        return () => {
            container.removeEventListener('click', handleConsentClick);
        };
    }, [cleanHtml]);

    return (
        <div
            ref={containerRef}
            className="safe-markdown-content"
            dangerouslySetInnerHTML={{ __html: cleanHtml }}
        />
    );
}

function getYoutubeIdFromUrl(url: string): string | null {
    try {
        const urlObj = new URL(url);
        if (urlObj.protocol !== 'https:') return null;

        const whiteListHosts = new Set([
            "www.youtube.com",
            "youtube.com",
            "www.youtube-nocookie.com",
            "youtube-nocookie.com"
        ]);

        if (!whiteListHosts.has(urlObj.hostname)) return null;

        let vidId: string | null = null;
        if (urlObj.pathname === '/watch') {
            vidId = urlObj.searchParams.get('v');
        } else if (urlObj.pathname.startsWith('/embed/')) {
            vidId = urlObj.pathname.split('/embed/')[1]?.split('/')[0] || null;
        }

        if (!vidId || !/^[a-zA-Z0-9_-]{11}$/.test(vidId)) return null;

        return vidId;
    } catch {
        return null;
    }
}
