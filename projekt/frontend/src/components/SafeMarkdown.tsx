import DOMPurify from "dompurify";
import {marked, type Tokens} from "marked";
import { useState, useEffect } from "react";

export function SafeMarkdown({ markdown }: { markdown: string }) {
    const [cleanHtml, setCleanHtml] = useState<string>("");
    const [hasAcceptedTerms, setHasAcceptedTerms] = useState<boolean>(false);


    useEffect(() => {
        const parseMarkdown = async () => {
            try {

                const renderer = new marked.Renderer();
                renderer.image = ({href, title, text}: Tokens.Image ) : string => {
                    if (!href) return '';


                    if (href.startsWith('embed:')) {
                        const url = href.replace(/^embed:/, '').trim();
                        const youtubeId = getYoutubeIdFromUrl(url);

                        if (!youtubeId) return `<p>Ungültiger YouTube-Link</p>`;

                        // TODO: Zustimmung für Datenschutz abfragen bevor eingebettet wird

                        return `<iframe src="https://www.youtube-nocookie.com/embed/${youtubeId}"
                                        title="YouTube video player"
                                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                                        referrerpolicy="strict-origin-when-cross-origin" allowfullscreen>                                        
                                </iframe>`
                    }
                    const titleAttr = title ? ` title="${title}"` : '';
                    const altAttr = text ? ` alt="${text}"` : '';
                    return `<img src="${href}" title="${titleAttr}" alt="${altAttr}"/>`;
                }





                const html = await marked(markdown, { renderer });

                /* Sanitize das HTML mit DOMPurify:
                 Gleiche Config wie beim Backend nur zusaätzlich
                 mit a, iframe und img */
                const clean = DOMPurify.sanitize(html, {
                    ALLOWED_TAGS: [
                        'b', 'em', 'i', 'strong', 'u',
                        'h1', 'h2', 'h3', 'h4', 'h5', 'h6',
                        'code', 'pre', 'br', 'hr',
                        'table', 'thead', 'tbody', 'tfoot', 'tr', 'td', 'th',
                        'ul', 'ol', 'li',
                        'a', 'iframe', 'img' // Additional tags
                        ],
                    ALLOWED_ATTR: ['href', 'src', 'alt', 'frameborder', 'allow', 'allowfullscreen', 'referrerpolicy'],
                    ALLOWED_URI_REGEXP: /^https?:/i
                });


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



function getYoutubeIdFromUrl(url: string): string | null {
    try {
        const urlObj = new URL(url);
        if (urlObj.protocol !== 'https:') return '';

        const whiteListHosts = new Set([
            "www.youtube.com",
            "youtube.com",
            "www.youtube-nocookie.com",
            "youtube-nocookie.com"
        ]);

        if (!whiteListHosts.has(urlObj.hostname)) return null;

        let vidId : string | null = null ;
        if (urlObj.pathname === '/watch') {
            vidId = urlObj.searchParams.get('v');
        } else if (urlObj.pathname.startsWith('/embed/')) {
            vidId = urlObj.pathname.split('/embed/')[2];
        }

        if (!vidId || !/^[a-zA-Z0-9_-]{11}$/.test(vidId)) return null;

        return vidId;
    } catch {
        return '';
    }
}